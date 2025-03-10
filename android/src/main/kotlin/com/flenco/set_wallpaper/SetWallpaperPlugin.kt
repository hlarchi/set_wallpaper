package com.flenco.set_wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File

class SetWallpaperPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(
          @NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
  ) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "set_wallpaper")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "setWallpaper" -> {
        try {
          val imagePath = call.argument<String>("imagePath")
          val wallpaperType = call.argument<Int>("wallpaperType") ?: WallpaperManager.FLAG_SYSTEM

          if (imagePath == null) {
            result.error("INVALID_ARGUMENT", "Image path cannot be null", null)
            return
          }

          val file = File(imagePath)
          if (!file.exists()) {
            result.error("FILE_NOT_FOUND", "Image file not found", null)
            return
          }

          val bitmap = BitmapFactory.decodeFile(imagePath)
          if (bitmap == null) {
            result.error("INVALID_IMAGE", "Could not decode image", null)
            return
          }

          val wallpaperManager = WallpaperManager.getInstance(context)
          // Get the display metrics to match screen dimensions
          val displayMetrics = context.resources.displayMetrics
          val width = displayMetrics.widthPixels
          val height = displayMetrics.heightPixels

          // Suggest dimensions to ensure proper scaling
          wallpaperManager.suggestDesiredDimensions(width, height)

          // Set the wallpaper with the visibleCrop parameter as null
          // The third parameter (allowBackup) is set to true
          wallpaperManager.setBitmap(bitmap, null, true, wallpaperType)
          result.success(true)
        } catch (e: Exception) {
          result.error("SET_WALLPAPER_ERROR", e.message, null)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
