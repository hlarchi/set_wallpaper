# 📱 Android Wallpaper Setter Plugin

A Flutter plugin for setting wallpapers on Android devices. Supports applying wallpapers to the **home screen**, **lock screen**, or **both**.

---

## 🚀 **Features**
- Set wallpapers for **home screen**, **lock screen**, or **both**.
- Simple and easy-to-use API.
- Supports selecting images from the gallery.
- Success and error notifications.

---

## 🛠️ **Installation**
Add to your `pubspec.yaml`:

```yaml
dependencies:
  set_wallpaper: ^1.0.0
  image_picker: ^1.0.7
```

Run:
```bash
flutter pub get
```

---

## 📚 **Usage**

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:set_wallpaper/set_wallpaper.dart';
import 'package:image_picker/image_picker.dart';

class WallpaperScreen extends StatefulWidget {
  @override
  _WallpaperScreenState createState() => _WallpaperScreenState();
}

class _WallpaperScreenState extends State<WallpaperScreen> {
  String? _selectedImagePath;

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final XFile? image = await picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      setState(() => _selectedImagePath = image.path);
    }
  }

  Future<void> _setWallpaper(WallpaperType type) async {
    if (_selectedImagePath == null) return;
    await SetWallpaper.setWallpaper(
      imagePath: _selectedImagePath!,
      wallpaperType: type,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Wallpaper Setter')),
      body: Column(
        children: [
          ElevatedButton(
            onPressed: _pickImage,
            child: Text('Pick Image'),
          ),
          ElevatedButton(
            onPressed: () => _setWallpaper(WallpaperType.home),
            child: Text('Set Home Screen Wallpaper'),
          ),
        ],
      ),
    );
  }
}
```

### Wallpaper Types
- `WallpaperType.system`: Home & Lock screens.
- `WallpaperType.home`: Home screen only.
- `WallpaperType.lock`: Lock screen only.

---

## 📝 **License**
MIT License.

---

## 🤝 **Contributing**
Open an issue or pull request on [GitHub](https://github.com/your_repo).

---

**Happy Coding! 🚀**

