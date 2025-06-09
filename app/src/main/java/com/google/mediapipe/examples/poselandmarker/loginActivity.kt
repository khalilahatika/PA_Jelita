package com.google.mediapipe.examples.poselandmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class loginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        db = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        nameEditText = findViewById(R.id.nameEditText)
        loginButton = findViewById(R.id.buttonStart)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val name = nameEditText.text.toString()

            if (email.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Email dan Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                addDataToFirestore(email, name)
            }
        }
    }

    private fun addDataToFirestore(email: String, name: String) {
        val user = User(email, name)

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                emailEditText.setText("")
                nameEditText.setText("")

                // Navigasi ke MainActivity setelah data tersimpan
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // opsional: untuk menutup loginActivity
            }
            .addOnFailureListener { e ->
                showErrorDialog("Terjadi kesalahan saat menyimpan data: ${e.message}")
            }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menyimpan Data")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                emailEditText.setText("")
                nameEditText.setText("")
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    data class User(
        var email: String = "",
        var name: String = ""
    )
}
