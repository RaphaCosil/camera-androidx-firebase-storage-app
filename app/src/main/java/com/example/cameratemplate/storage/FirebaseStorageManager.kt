package com.example.cameratemplate.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FirebaseStorageManager {

    fun uploadImageToFirebase(context: Context, imageView: ImageView, onUploadComplete: (String) -> Unit) {

        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
        if (bitmap == null) {
            Toast.makeText(context, "Image not available for upload", Toast.LENGTH_SHORT).show()
            return
        }

        val byteArrayOutputStream = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, this)
        }
        val dataBytes = byteArrayOutputStream.toByteArray()

        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReference("gallery").child("img_of_camera_template_${System.currentTimeMillis()}.jpg")

        ref.putBytes(dataBytes)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    onUploadComplete(uri.toString())
                    Log.d("FirebaseStorageManager", "Image uploaded successfully: $uri")
                    Toast.makeText(context, "Successfully uploaded image!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.d("FirebaseStorageManager", "Failed to upload image: ${it.message}")
                Toast.makeText(context, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun loadImageFromFirebase(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(Uri.parse(imageUrl))
            .error(android.R.drawable.ic_dialog_alert) // Placeholder on error
            .into(imageView)
    }
}
