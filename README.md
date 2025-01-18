# Camera Androidx & Firebase Storage App

This is an Android application template for capturing and uploading images. It includes the following features:

## Features

- **Photo Capture:** Capture photos using CameraX, automatically adjust rotation, and save them to the device.
- **Camera Switching:** Switch between front and rear cameras.
- **Image Selection:** Access the gallery to select an image.
- **Firebase Upload:** Upload images to Firebase Storage and generate URLs.
- **Image Display:** Use Glide to load and display images from Firebase.

---

## Technical Structure

### CameraManager

Manages camera operations, including:

- **Photo Capture:** Capture and save photos.
- **Camera Switching:** Switch between front and rear cameras.

### FirebaseStorageManager

Handles image-related operations in Firebase Storage, including:

- **Upload Images:** Upload photos to Firebase Storage.
- **Download Images:** Retrieve image URLs for display.

### MainActivity

- **Permission Management:** Manages camera and gallery permissions.
- **Manager Initialization:** Initializes `CameraManager` and `FirebaseStorageManager`.

---

## Permissions

The app requests the following permissions to function properly:

- **Camera Access:** Required for capturing photos.
- **Gallery Access:** Required for selecting images.
- **Internet Access:** Required for Firebase operations.

---

## How to Use

1. Clone the repository and open the project in Android Studio.
2. Configure Firebase:
    - Create a Firebase project in the Firebase Console.
    - Add your Android app to the project.
    - Download the `google-services.json` file and place it in the `app` directory.
    - Add Firebase dependencies to the `build.gradle` files.
3. Build and run the application.

---
