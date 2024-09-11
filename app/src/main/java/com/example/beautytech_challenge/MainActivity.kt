package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.main_activity)

        // Encontre o botão com o ID btn_login
        val loginButton: Button = findViewById(R.id.btn_login)

        // Configure o listener de clique para o botão
        loginButton.setOnClickListener {
            // Crie um Intent para iniciar a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }






    }

}