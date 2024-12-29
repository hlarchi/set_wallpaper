import 'package:flutter_test/flutter_test.dart';
import 'package:set_wallpaper/set_wallpaper.dart';
import 'package:set_wallpaper/set_wallpaper_platform_interface.dart';
import 'package:set_wallpaper/set_wallpaper_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSetWallpaperPlatform
    with MockPlatformInterfaceMixin
    implements SetWallpaperPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SetWallpaperPlatform initialPlatform = SetWallpaperPlatform.instance;

  test('$MethodChannelSetWallpaper is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSetWallpaper>());
  });

  test('getPlatformVersion', () async {
    SetWallpaper setWallpaperPlugin = SetWallpaper();
    MockSetWallpaperPlatform fakePlatform = MockSetWallpaperPlatform();
    SetWallpaperPlatform.instance = fakePlatform;

    expect(await setWallpaperPlugin.getPlatformVersion(), '42');
  });
}
