# Camera-Androidx-Firebase-App
This application is an Android camera template with the following features:

Features:
- Photo Capture: Capture photos using CameraX, automatically adjust rotation, and save them to the device.
- Camera Switching: Switch between front and rear cameras.
- Image Selection: Access the gallery to select an image.
- Firebase Upload: Upload images to Firebase Storage and generate URLs.
- Image Display: Use Glide to load and display images from Firebase.

### Technical Structure:
- CameraManager: Manages camera operations, photo capture, and camera switching.
- FirebaseStorageManager: Handles image upload and download in Firebase Storage.
- MainActivity: Manages permissions and initializes the managers.

### Permissions:
The app requests permissions for the camera, gallery, and audio recording to ensure proper functionality on Android devices.
