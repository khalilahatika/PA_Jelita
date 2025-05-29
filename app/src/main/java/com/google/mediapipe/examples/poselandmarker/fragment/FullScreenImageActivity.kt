/*
 * Activity untuk menampilkan gambar secara fullscreen dengan fitur delete
 */
package com.google.mediapipe.examples.poselandmarker.fragment

import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.mediapipe.examples.poselandmarker.R

// Binding class untuk layout fullscreen
class FullscreenImageBinding private constructor(
    private val rootView: ConstraintLayout,
    val imageFullscreen: ImageView,
    val btnBack: ImageButton,
    val btnDelete: ImageButton
) : ViewBinding {

    override fun getRoot(): ConstraintLayout = rootView

    companion object {
        fun inflate(inflater: LayoutInflater): FullscreenImageBinding {
            val root = ConstraintLayout(inflater.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.BLACK)
            }

            val imageView = ImageView(inflater.context).apply {
                id = android.view.View.generateViewId()
                scaleType = ImageView.ScaleType.FIT_CENTER
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
            }

            val btnBack = ImageButton(inflater.context).apply {
                id = android.view.View.generateViewId()
                setImageResource(android.R.drawable.ic_menu_revert)
                background = null
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    setMargins(32, 64, 0, 0)
                }
            }

            val btnDelete = ImageButton(inflater.context).apply {
                id = android.view.View.generateViewId()
                setImageResource(android.R.drawable.ic_menu_delete)
                background = null
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    setMargins(0, 64, 32, 0)
                }
            }

            root.addView(imageView)
            root.addView(btnBack)
            root.addView(btnDelete)

            return FullscreenImageBinding(root, imageView, btnBack, btnDelete)
        }
    }
}

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: FullscreenImageBinding
    private var imageUri: Uri? = null

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val RESULT_IMAGE_DELETED = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide system UI untuk fullscreen
        hideSystemUI()

        // Ambil URI gambar dari intent
        imageUri = intent.getParcelableExtra(EXTRA_IMAGE_URI)

        imageUri?.let { uri ->
            binding.imageFullscreen.setImageURI(uri)
        }

        // Setup button listeners
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Klik pada gambar untuk toggle system UI
        binding.imageFullscreen.setOnClickListener {
            toggleSystemUI()
        }
    }

    private fun hideSystemUI() {
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun toggleSystemUI() {
        val isFullscreen = (window.decorView.systemUiVisibility
                and android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) != 0

        if (isFullscreen) {
            showSystemUI()
        } else {
            hideSystemUI()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Hapus Foto")
            .setMessage("Apakah Anda yakin ingin menghapus foto ini? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ -> deleteImage() }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }

    private fun deleteImage() {
        imageUri?.let { uri ->
            try {
                val rowsDeleted = contentResolver.delete(uri, null, null)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Foto berhasil dihapus", Toast.LENGTH_SHORT).show()

                    // Set result untuk memberitahu gallery bahwa foto telah dihapus
                    setResult(RESULT_IMAGE_DELETED)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menghapus foto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}