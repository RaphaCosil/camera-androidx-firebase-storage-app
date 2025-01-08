package com.example.cameratemplate.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.OrientationEventListener
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cameratemplate.camera.CameraManager
import com.example.cameratemplate.databinding.ActivityMainBinding
import com.example.cameratemplate.storage.FirebaseStorageManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager
    private lateinit var firebaseStorageManager: FirebaseStorageManager

    private var docData: MutableMap<String, String> = mutableMapOf()

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { uri ->
                binding.imgvPhoto.apply {
                    visibility = View.VISIBLE
                    setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseStorageManager = FirebaseStorageManager()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            cameraManager = CameraManager(cameraProvider, binding.cameraPreview)

            if (allPermissionsGranted()) {
                cameraManager.startCamera()

                setupOrientationListener()
            } else {
                requestPermissions()
            }

            binding.btCamera.setOnClickListener {
                cameraManager.takePhoto(
                    contentResolver = contentResolver,
                    onSuccess = { uri ->
                        binding.imgvPhoto.apply {
                            visibility = View.VISIBLE
                            setImageURI(uri)
                        }
                        firebaseStorageManager.uploadImageToFirebase(this, binding.imgvPhoto) { url ->
                            docData["url"] = url
                        }
                    },
                    onFailure = {
                        Toast.makeText(this, "Error capturing image", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            binding.btExchange.setOnClickListener {
                cameraManager.switchCamera()
            }

            binding.btImageGallery.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            }

            binding.btFirebase.setOnClickListener {
                binding.imgvPhoto.visibility = View.VISIBLE
                firebaseStorageManager.loadImageFromFirebase(binding.imgvPhoto, docData["url"] ?: "")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupOrientationListener() {
        val orientationListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = cameraManager.adjustImageRotation(orientation)
                cameraManager.setTargetRotation(rotation)
            }
        }
        orientationListener.enable()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissions(REQUIRED_PERMISSIONS, 0)
    }
}
