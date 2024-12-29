# 📱 **Set Wallpaper**

A **Flutter plugin** for setting wallpapers on **Android devices**. Supports applying wallpapers to the **home screen**, **lock screen**, or **both** with a simple and intuitive API.

---

## 🚀 **Features**
- Set wallpapers for **Home Screen**, **Lock Screen**, or **Both**.
- Simple and user-friendly API.
- Image selection directly from the **gallery**.
- Real-time success and error notifications.

---

## 🛠️ **Installation**
Add the following dependencies to your `pubspec.yaml`:

```yaml
dependencies:
  set_wallpaper: ^1.0.0
  image_picker: ^1.0.7
```

Run the following command to fetch the packages:

```bash
flutter pub get
```

---

## 📚 **Usage Example**

```dart
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:set_wallpaper/set_wallpaper.dart';
import 'package:image_picker/image_picker.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
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
      appBar: AppBar(title: const Text('Set Wallpaper')),
      body: Column(
        children: [
          ElevatedButton(
            onPressed: _pickImage,
            child: const Text('Pick Image'),
          ),
          ElevatedButton(
            onPressed: () => _setWallpaper(WallpaperType.home),
            child: const Text('Set Home Screen Wallpaper'),
          ),
          ElevatedButton(
            onPressed: () => _setWallpaper(WallpaperType.lock),
            child: const Text('Set Lock Screen Wallpaper'),
          ),
          ElevatedButton(
            onPressed: () => _setWallpaper(WallpaperType.system),
            child: const Text('Set Both Screens Wallpaper'),
          ),
        ],
      ),
    );
  }
}
```

### **Wallpaper Types:**
- `WallpaperType.system`: Home & Lock screens.
- `WallpaperType.home`: Home screen only.
- `WallpaperType.lock`: Lock screen only.

---

## 📝 **License**
This project is licensed under the **MIT License**.

---

## 🤝 **Contributing**
Feel free to contribute! Open an issue or submit a pull request on [GitHub](https://github.com/flenco-in/set_wallpaper).

---

**Happy Coding! 🚀**

