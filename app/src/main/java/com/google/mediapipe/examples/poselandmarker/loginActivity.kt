package com.google.mediapipe.examples.poselandmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class loginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        val buttonKirim: Button = findViewById(R.id.buttonStart)

        buttonKirim.setOnClickListener {
            // Navigasi ke aktivitas lain (misalnya, Activity2)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}