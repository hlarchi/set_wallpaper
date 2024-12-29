import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'set_wallpaper_method_channel.dart';

abstract class SetWallpaperPlatform extends PlatformInterface {
  /// Constructs a SetWallpaperPlatform.
  SetWallpaperPlatform() : super(token: _token);

  static final Object _token = Object();

  static SetWallpaperPlatform _instance = MethodChannelSetWallpaper();

  /// The default instance of [SetWallpaperPlatform] to use.
  ///
  /// Defaults to [MethodChannelSetWallpaper].
  static SetWallpaperPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SetWallpaperPlatform] when
  /// they register themselves.
  static set instance(SetWallpaperPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
