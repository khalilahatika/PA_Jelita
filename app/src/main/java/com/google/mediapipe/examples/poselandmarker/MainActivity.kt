package com.google.mediapipe.examples.poselandmarker

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var overlayView: OverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        overlayView = findViewById(R.id.overlay_view)
        val fragmentContainer = findViewById<View>(R.id.fragment_container)

        val btnStar = findViewById<ImageButton>(R.id.btnStar)
        val btnCamera = findViewById<ImageButton>(R.id.btncamerar)
        val btnHanger = findViewById<ImageButton>(R.id.btndrop)
        val btnUndo = findViewById<ImageButton>(R.id.btnUndo)
        val btnGallery = findViewById<ImageButton>(R.id.btnImage)

        btnStar.setOnClickListener {
            // TODO: implement star action
        }

        btnCamera.setOnClickListener {
            captureAndSave(fragmentContainer)
        }

        btnHanger.setOnClickListener {
            overlayView.nextDress()
        }

        btnUndo.setOnClickListener {
            finish()
        }

        btnGallery.setOnClickListener {
            // TODO: implement gallery action
        }
    }

    private fun captureAndSave(targetView: View) {
        val width = targetView.width
        val height = targetView.height
        if (width == 0 || height == 0) return

        // Create bitmap and canvas
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        targetView.draw(canvas)
        overlayView.draw(canvas)

        try {
            val filename = "screenshot_${System.currentTimeMillis()}.png"
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for API 29+
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseLandmarker")
                }
                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
                // Legacy storage for API <29
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val imageFile = File(picturesDir, filename)
                FileOutputStream(imageFile)
            }

            fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            Toast.makeText(this, "Screenshot tersimpan", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        finish() }
}