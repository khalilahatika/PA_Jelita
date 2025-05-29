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
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.mediapipe.examples.poselandmarker.fragment.CameraFragment
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
        val btnOptionC = findViewById<ImageButton>(R.id.btnOptionC)
        val btnOptionD = findViewById<ImageButton>(R.id.btnOptionD)


        // Set semua tombol opsi tersembunyi saat mulai
        btnOptionA.visibility = View.GONE
        btnOptionB.visibility = View.GONE
        btnOptionC.visibility = View.GONE
        btnOptionD.visibility = View.GONE
        isOptionsVisible = false

        // Navigasi tombol Gallery
        btnGallery.setOnClickListener {
            val navController = findNavController(R.id.fragment_container)
            navController.navigate(R.id.gallery_fragment)
        }

        btnStar.setOnClickListener {
            // TODO: implement star action
        }

        btnCamera.setOnClickListener {
            captureAndSave() // Tanpa parameter
        }

        btnHanger.setOnClickListener {
            if (!isOptionsVisible) {
                // Tampilkan semua tombol opsi
                btnOptionA.visibility = View.VISIBLE
                btnOptionB.visibility = View.VISIBLE
                btnOptionC.visibility = View.VISIBLE
                btnOptionD.visibility = View.VISIBLE
            } else {
                // Sembunyikan semua tombol opsi
                btnOptionA.visibility = View.GONE
                btnOptionB.visibility = View.GONE
                btnOptionC.visibility = View.GONE
                btnOptionD.visibility = View.GONE
            }
            isOptionsVisible = !isOptionsVisible
        }

        btnOptionA.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                toggleDress(0)
                setDressByIndex(0)
                Log.d(TAG, "btnOptionA clicked - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress A")
            }
            Toast.makeText(this, "Opsi A dipilih", Toast.LENGTH_SHORT).show()
        }

        btnOptionB.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                toggleDress(1)
                setDressByIndex(1)
                Log.d(TAG, "btnOptionB clicked - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress B")
            }
            Toast.makeText(this, "Opsi B dipilih", Toast.LENGTH_SHORT).show()
        }

        btnOptionC.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                toggleDress(2)
                setDressByIndex(2)
                Log.d(TAG, "btnOptionC clicked - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress C")
            }
            Toast.makeText(this, "Opsi C dipilih", Toast.LENGTH_SHORT).show()
        }

        btnOptionD.setOnClickListener {
            getOverlayViewFromFragment()?.apply {
                toggleDress(3)
                setDressByIndex(3)
                Log.d(TAG, "btnOptionD clicked - instance: $this")
            } ?: run {
                Log.e(TAG, "OverlayView is null when trying to set dress D")
            }
            Toast.makeText(this, "Opsi D dipilih", Toast.LENGTH_SHORT).show()
        }

        btnUndo.setOnClickListener {
            finish()
        }

        // Tambahkan logika untuk hide UI saat GalleryFragment muncul
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleDestinationChange(destination, btnStar, btnCamera, btnHanger, btnUndo, btnGallery, btnOptionA, btnOptionB, btnOptionC, btnOptionD)
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
        buttons.find { it.id == R.id.btnOptionC }?.visibility = View.GONE
        buttons.find { it.id == R.id.btnOptionD }?.visibility = View.GONE

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

    private fun captureAndSave() {
        // 1. Dapatkan referensi ke CameraFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment

        if (currentFragment !is CameraFragment) {
            Toast.makeText(this, "Anda harus berada di mode kamera", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Dapatkan TextureView/SurfaceView dari viewFinder
        val viewFinder = currentFragment.fragmentCameraBinding.viewFinder

        // 3. Dapatkan overlay view
        val overlayView = getOverlayViewFromFragment()

        // 4. Pastikan keduanya ada
        if (viewFinder == null || overlayView == null) {
            Toast.makeText(this, "Tidak dapat mengambil gambar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // 5. Dapatkan bitmap dari preview kamera - LETAKKAN KODE TERSEBUT DI SINI
            val viewFinderBitmap = viewFinder.bitmap


            if (viewFinderBitmap == null) {
                Toast.makeText(this, "Tidak dapat mengambil gambar dari kamera", Toast.LENGTH_SHORT).show()
                return
            }

            // 6. Buat bitmap baru dengan ukuran yang sama
            val resultBitmap = Bitmap.createBitmap(
                viewFinderBitmap.width,
                viewFinderBitmap.height,
                Bitmap.Config.ARGB_8888
            )


            // 7. Buat canvas dari bitmap hasil
            val canvas = Canvas(resultBitmap)

            // 8. Gambar preview kamera ke canvas
            canvas.drawBitmap(viewFinderBitmap, 0f, 0f, null)

            // 9. Ukur dan letakkan overlay di posisi yang benar
            overlayView.measure(
                View.MeasureSpec.makeMeasureSpec(viewFinderBitmap.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewFinderBitmap.height, View.MeasureSpec.EXACTLY)
            )
            overlayView.layout(0, 0, viewFinderBitmap.width, viewFinderBitmap.height)

            // 10. Gambar overlay ke canvas yang sama
            overlayView.draw(canvas)

            // 11. Simpan bitmap hasil ke galeri
            saveToGallery(resultBitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal mengambil gambar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val PreviewView.bitmap: Bitmap?
        get() {
            return Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888).also {
                val canvas = Canvas(it)
                this.draw(canvas)
            }
        }
    // Metode terpisah untuk menyimpan bitmap ke galeri
    private fun saveToGallery(bitmap: Bitmap) {
        try {
            val filename = "pose_landmarker_${System.currentTimeMillis()}.png"
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PoseLandmarker")
                }
                val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(imagesDir, "PoseLandmarker")
                if (!appDir.exists()) appDir.mkdirs()
                val imageFile = File(appDir, filename)
                FileOutputStream(imageFile)
            }

            fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            Toast.makeText(this, "Gambar tersimpan ke galeri", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    // Extensi untuk mendapatkan bitmap dari TextureView/SurfaceView
    private val TextureView.bitmap: Bitmap?
        get() {
            if (!isAvailable) return null

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)
            return bitmap
        }


    override fun onBackPressed() {
        finish()
    }
}
