package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class RegisterActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.register_layout)

        val backButton = findViewById<Button>(R.id.btn_back)

        // Configure o listener de clique para o botão
        backButton.setOnClickListener {
            // Crie um Intent para iniciar a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}