package com.example.yourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.mediapipe.examples.poselandmarker.OverlayView
import com.google.mediapipe.examples.poselandmarker.R

class welcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen)

        val buttonMulai: Button = findViewById(R.id.buttonStart)
        val buttonKeluar: Button = findViewById(R.id.buttonExit)

        buttonMulai.setOnClickListener {
            // Navigasi ke aktivitas lain (misalnya, Activity2)
            val intent = Intent(this, OverlayView::class.java)
            startActivity(intent)
        }

        buttonKeluar.setOnClickListener {
            // Keluar dari aplikasi
            finish()
        }
    }
}