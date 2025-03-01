package com.google.mediapipe.examples.poselandmarker.fragment

import com.google.mediapipe.examples.poselandmarker.MainActivity
import com.google.mediapipe.examples.poselandmarker.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen) // Menggunakan layout yang telah Anda buat

        // Mengatur listener untuk tombol "Mulai"
        findViewById<View>(R.id.buttonStart).setOnClickListener {
            // Pindah ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Tutup WelcomeActivity agar tidak bisa kembali ke sini
        }

        // Mengatur listener untuk tombol "Keluar"
        findViewById<View>(R.id.buttonExit).setOnClickListener {
            // Keluar dari aplikasi
            finishAffinity() // Menutup semua activity dan keluar dari aplikasi
        }
    }
}
