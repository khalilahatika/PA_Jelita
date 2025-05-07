package com.google.mediapipe.examples.poselandmarker

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var overlayView: OverlayView? = null
    private var isOptionsVisible = false
    private val TAG = "MainActivity"

    // Interface to get a reference to the OverlayView from the CameraFragment
    interface OverlayViewProvider {
        fun getOverlayView(): OverlayView?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentContainer = findViewById<View>(R.id.fragment_container)

        val btnStar = findViewById<ImageButton>(R.id.btnStar)
        val btnCamera = findViewById<ImageButton>(R.id.btncamerar)
        val btnHanger = findViewById<ImageButton>(R.id.btndrop)
        val btnUndo = findViewById<ImageButton>(R.id.btnUndo)
        val btnGallery = findViewById<ImageButton>(R.id.btnImage)
        val btnOptionA = findViewById<ImageButton>(R.id.btnOptionA)
        val btnOptionB = findViewById<ImageButton>(R.id.btnOptionB)

        // Navigasi tombol Gallery
        btnGallery.setOnClickListener {
            val navController = findNavController(R.id.fragment_container)
            navController.navigate(R.id.gallery_fragment)
        }

        btnStar.setOnClickListener {
            // TODO: implement star action
        }

        btnCamera.setOnClickListener {
            captureAndSave(fragmentContainer)
        }

        btnHanger.setOnClickListener {
            if (!isOptionsVisible) {
                // Tampilkan tombol opsi
                btnOptionA.visibility = View.VISIBLE
                btnOptionB.visibility = View.VISIBLE
            } else {
                // Sembunyikan tombol opsi
                btnOptionA.visibility = View.GONE
                btnOptionB.visibility = View.GONE
            }
            isOptionsVisible = !isOptionsVisible
        }

        btnOptionA.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                showDressA(0)
                setDressByIndex(0)
                Log.d(TAG, "btnOptionA klik - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress A")
            }
            Toast.makeText(this, "Opsi A dipilih", Toast.LENGTH_SHORT).show()
            btnOptionA.visibility = View.GONE
            btnOptionB.visibility = View.GONE
            isOptionsVisible = false
        }

        btnOptionB.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                showDressB(1)
                setDressByIndex(1)
                Log.d(TAG, "btnOptionB klik - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress B")
            }
            Toast.makeText(this, "Opsi B dipilih", Toast.LENGTH_SHORT).show()
            btnOptionA.visibility = View.GONE
            btnOptionB.visibility = View.GONE
            isOptionsVisible = false
        }

        btnUndo.setOnClickListener {
            finish()
        }

        // Tambahkan logika untuk hide UI saat GalleryFragment muncul
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleDestinationChange(destination, btnStar, btnCamera, btnHanger, btnUndo, btnGallery, btnOptionA, btnOptionB)
        }
    }

    private fun handleDestinationChange(
        destination: NavDestination,
        vararg buttons: ImageButton
    ) {
        val isGalleryFragment = destination.id == R.id.gallery_fragment
        val visibility = if (isGalleryFragment) View.GONE else View.VISIBLE

        // Update UI visibility based on navigation
        buttons.forEach { it.visibility = visibility }

        // Always hide option buttons when navigating
        buttons.find { it.id == R.id.btnOptionA }?.visibility = View.GONE
        buttons.find { it.id == R.id.btnOptionB }?.visibility = View.GONE
        isOptionsVisible = false
    }

    private fun getOverlayViewFromFragment(): OverlayView? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment

        if (currentFragment is OverlayViewProvider) {
            return currentFragment.getOverlayView()
        }

        Log.e(TAG, "Could not get OverlayView from fragment: ${currentFragment?.javaClass?.simpleName}")
        return null
    }

    private fun captureAndSave(targetView: View) {
        val width = targetView.width
        val height = targetView.height
        if (width == 0 || height == 0) return

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        targetView.draw(canvas)

        // Draw overlay if available
        getOverlayViewFromFragment()?.draw(canvas)

        try {
            val filename = "screenshot_${System.currentTimeMillis()}.png"
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseLandmarker")
                }
                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
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
        finish()
    }
}