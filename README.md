# 📱 Android Wallpaper Setter Plugin

A Flutter plugin for setting wallpapers on Android devices effortlessly. Supports applying wallpapers to the **home screen**, **lock screen**, or **both**.

---

## 🚀 **Features**
- Set wallpapers for **home screen**, **lock screen**, or **both**.
- Simple and easy-to-use API.
- Supports selecting images from the device gallery.
- Displays success or error notifications.

---

## 🛠️ **Installation**

Add the following dependencies to your `pubspec.yaml`:

```yaml
dependencies:
  set_wallpaper: ^1.0.0
  image_picker: ^1.0.7
  path_provider: ^2.1.2
```

Run:
```bash
flutter pub get
```

---

## 📚 **Usage**

### 1. Import Required Packages
```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:set_wallpaper/set_wallpaper.dart';
import 'package:image_picker/image_picker.dart';
```

### 2. Example Implementation

```dart
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

### 3. Wallpaper Types
- `WallpaperType.system`: Set wallpaper for both home and lock screens.
- `WallpaperType.home`: Set wallpaper for the home screen only.
- `WallpaperType.lock`: Set wallpaper for the lock screen only.

---

## 📊 **API Reference**

### `SetWallpaper.setWallpaper`
- **Parameters:**
    - `imagePath`: Local path to the image file.
    - `wallpaperType`: Enum (`system`, `home`, `lock`).
- **Returns:** `Future<void>`

### `SetWallpaper.getPlatformVersion`
- **Returns:** `Future<String?>`
- **Description:** Retrieves the platform version.

---

## 🧪 **Testing**
Ensure the plugin is properly set up:
```bash
flutter run
```

---

## 📝 **License**
This project is licensed under the **MIT License**.

---

## 🤝 **Contributing**
Contributions are welcome! Please open an issue or pull request on the [GitHub repository](https://github.com/flenco-in/set_wallpaper.git).

---

## 📬 **Support**
For any issues or questions, feel free to raise an issue or reach out via email.

**Happy Coding! 🚀**

