package com.google.mediapipe.examples.poselandmarker.fragment

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mediapipe.examples.poselandmarker.R

@Composable
fun WelcomeScreen() {
    AndroidView(factory = { context ->
        // Inflate the XML layout
        LayoutInflater.from(context).inflate(R.layout.welcome_screen, null)
    }, modifier = Modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    MaterialTheme {
        WelcomeScreen()
    }
}
