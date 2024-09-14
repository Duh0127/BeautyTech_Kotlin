package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.beautytech_challenge.activities.LoginActivity
import com.example.beautytech_challenge.activities.ProductsActivity
import com.example.beautytech_challenge.activities.ProfileActivity

class MainActivity : Activity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.main_layout)

        val menuProdutos = findViewById<Button>(R.id.btn_produtos)
        menuProdutos.setOnClickListener {
            val productsIntent = Intent(this, ProductsActivity::class.java)
            startActivity(productsIntent)
        }

        val btnHigiene = findViewById<Button>(R.id.btn_higiene)
        val btnCabelos = findViewById<Button>(R.id.btn_cabelos)
        val btnUnhas = findViewById<Button>(R.id.btn_unhas)
        val btnPerfumes = findViewById<Button>(R.id.btn_perfumes)

        val productIntent = Intent(this, ProductsActivity::class.java)
        btnHigiene.setOnClickListener {
            startActivity(productIntent)
        }

        btnCabelos.setOnClickListener {
            startActivity(productIntent)
        }

        btnUnhas.setOnClickListener {
            startActivity(productIntent)
        }

        btnPerfumes.setOnClickListener {
            startActivity(productIntent)
        }

        updateLoginButton()
    }

    override fun onStart() {
        super.onStart()
        updateLoginButton()
    }

    private fun updateLoginButton() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("userData", null)

        val loginButton: Button = findViewById(R.id.btn_login)
        if (userDataString != null) {
            loginButton.text = "Perfil"
            loginButton.setOnClickListener {
                val profileIntent = Intent(this, ProfileActivity::class.java)
                startActivity(profileIntent)
            }
        } else {
            loginButton.text = "Login"
            loginButton.setOnClickListener {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }
}