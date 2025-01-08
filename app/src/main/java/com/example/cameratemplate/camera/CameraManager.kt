package com.example.cameratemplate.camera

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import java.util.Locale

class CameraManager(
    private val cameraProvider: ProcessCameraProvider,
    private val viewFinder: PreviewView
) {
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture

    // Function to start the camera preview
    fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(viewFinder.surfaceProvider)
        }
        // Initialize imageCapture
        imageCapture = ImageCapture.Builder().build()

        // Unbind all use cases before binding new ones
        cameraProvider.unbindAll()

        // Bind the camera lifecycle and use cases to the camera
        cameraProvider.bindToLifecycle(
            viewFinder.context as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    // Function to switch between front and back cameras
    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()  // Restart the camera to apply the camera switch
    }

    // Function to capture a photo
    fun takePhoto(
        contentResolver: ContentResolver,
        onSuccess: (Uri?) -> Unit,
        onFailure: (ImageCaptureException) -> Unit
    ) {
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        // Define metadata and output location for the captured photo
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Take the picture and handle success or failure callbacks
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(viewFinder.context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess(output.savedUri)  // Return the URI of the saved image
                }

                override fun onError(exception: ImageCaptureException) {
                    onFailure(exception)  // Handle the image capture error
                }
            }
        )
    }

    // Function to adjust image rotation based on device orientation
    fun adjustImageRotation(orientation: Int): Int {
        return when (orientation) {
            in 45..134 -> Surface.ROTATION_90
            in 135..224 -> Surface.ROTATION_180
            in 225..314 -> Surface.ROTATION_270
            else -> Surface.ROTATION_0
        }
    }

    // Function to set the target rotation for image capture
    fun setTargetRotation(rotation: Int) {
        // Ensure imageCapture is initialized before setting the target rotation
        if (::imageCapture.isInitialized) {
            imageCapture.targetRotation = rotation
        }
    }
}
