import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'set_wallpaper_platform_interface.dart';

/// An implementation of [SetWallpaperPlatform] that uses method channels.
class MethodChannelSetWallpaper extends SetWallpaperPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('set_wallpaper');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
