package com.flenco.set_wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Display
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SetWallpaperPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  // Decoding and WallpaperManager.setBitmap can take seconds on large images;
  // they must never run on the platform (main) thread or Android raises an ANR.
  // A single thread also serialises concurrent calls.
  private var executor: ExecutorService? = null
  private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    executor = Executors.newSingleThreadExecutor()
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "set_wallpaper")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "setWallpaper" -> {
        val imagePath = call.argument<String>("imagePath")
        val wallpaperType = call.argument<Int>("wallpaperType") ?: WallpaperManager.FLAG_SYSTEM

        if (imagePath == null) {
          result.error("INVALID_ARGUMENT", "Image path cannot be null", null)
          return
        }

        val worker = executor
        if (worker == null || worker.isShutdown) {
          result.error("SET_WALLPAPER_ERROR", "Plugin is detached from the engine", null)
          return
        }

        worker.execute {
          try {
            setWallpaper(imagePath, wallpaperType)
            mainHandler.post { result.success(true) }
          } catch (e: WallpaperException) {
            mainHandler.post { result.error(e.code, e.message, null) }
          } catch (e: OutOfMemoryError) {
            mainHandler.post { result.error("OUT_OF_MEMORY", e.message, null) }
          } catch (e: Exception) {
            mainHandler.post { result.error("SET_WALLPAPER_ERROR", e.message, null) }
          }
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun setWallpaper(imagePath: String, wallpaperType: Int) {
    if (!File(imagePath).exists()) {
      throw WallpaperException("FILE_NOT_FOUND", "Image file not found")
    }

    val (screenWidth, screenHeight) = portraitScreenSize()
    val bitmap = decodeScaledToScreen(imagePath, screenWidth, screenHeight)
      ?: throw WallpaperException("DECODE_ERROR", "Unable to decode image")

    try {
      val wallpaperManager = WallpaperManager.getInstance(context)
      wallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        wallpaperManager.setBitmap(bitmap, null, true, wallpaperType)
      } else {
        wallpaperManager.setBitmap(bitmap)
      }
    } finally {
      bitmap.recycle()
    }
  }

  /**
   * Decodes [imagePath] subsampled, then centre-crops and scales it to exactly
   * [targetWidth] x [targetHeight] so WallpaperManager only has to compress and
   * transfer a screen-sized bitmap.
   */
  private fun decodeScaledToScreen(imagePath: String, targetWidth: Int, targetHeight: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(imagePath, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    val decoded = BitmapFactory.decodeFile(
      imagePath,
      BitmapFactory.Options().apply {
        inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight, targetWidth, targetHeight)
      },
    ) ?: return null

    val targetRatio = targetHeight.toFloat() / targetWidth.toFloat()
    val sourceRatio = decoded.height.toFloat() / decoded.width.toFloat()

    val cropWidth: Int
    val cropHeight: Int
    if (sourceRatio > targetRatio) {
      cropWidth = decoded.width
      cropHeight = min(decoded.height, (decoded.width * targetRatio).roundToInt())
    } else {
      cropHeight = decoded.height
      cropWidth = min(decoded.width, (decoded.height / targetRatio).roundToInt())
    }
    val x = (decoded.width - cropWidth) / 2
    val y = (decoded.height - cropHeight) / 2

    // Never upscale: a smaller source is handed over as-is (cropped only).
    val scale = min(1f, targetWidth.toFloat() / cropWidth.toFloat())
    val matrix = Matrix().apply { if (scale < 1f) setScale(scale, scale) }

    val result = Bitmap.createBitmap(decoded, x, y, cropWidth, cropHeight, matrix, true)
    if (result !== decoded) decoded.recycle()
    return result
  }

  /** Largest power-of-two sample size that keeps the decoded image covering the target. */
  private fun sampleSizeFor(width: Int, height: Int, targetWidth: Int, targetHeight: Int): Int {
    var sampleSize = 1
    while (width / (sampleSize * 2) >= targetWidth && height / (sampleSize * 2) >= targetHeight) {
      sampleSize *= 2
    }
    return sampleSize
  }

  private fun portraitScreenSize(): Pair<Int, Int> {
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    val size = Point()
    if (display != null) {
      @Suppress("DEPRECATION")
      display.getRealSize(size)
    }
    if (size.x <= 0 || size.y <= 0) {
      val metrics = context.resources.displayMetrics
      size.set(metrics.widthPixels, metrics.heightPixels)
    }
    return Pair(min(size.x, size.y), max(size.x, size.y))
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    executor?.shutdown()
    executor = null
  }

  private class WallpaperException(val code: String, message: String) : Exception(message)
}
