package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LoginActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.login_activity)

        val registerButton = findViewById<Button>(R.id.btn_register)

        // Configure o listener de clique para o botão
        registerButton.setOnClickListener {
            // Crie um Intent para iniciar a LoginActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val mainButton = findViewById<Button>(R.id.btn_back)
        mainButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}