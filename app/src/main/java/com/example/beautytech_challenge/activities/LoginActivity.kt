package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.repositories.LoginRepository
import org.json.JSONObject

class LoginActivity : Activity() {

    private lateinit var progressBar: ProgressBar
    private val loginRepository = LoginRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login_layout)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        progressBar = findViewById(R.id.loading_spinner)

        loginButton.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtSenha.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            loginRepository.login(email, password) { jsonObject, errorMessage ->
                runOnUiThread {
                    progressBar.visibility = View.GONE

                    if (jsonObject != null) {
                        handleSuccessfulLogin(jsonObject)
                    } else if (errorMessage != null) {
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val mainButton = findViewById<Button>(R.id.btn_back)
        mainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleSuccessfulLogin(jsonObject: JSONObject) {
        val usuario = jsonObject.getJSONObject("usuario")
        val nomeUsuario = usuario.getString("nome")
        val token = jsonObject.getString("token")

        Log.d("LOGIN", "Login Efetuado com sucesso: $nomeUsuario")

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userData", jsonObject.toString())
        editor.putString("token", token)
        editor.apply()

        Toast.makeText(this, "Bem-vindo $nomeUsuario", Toast.LENGTH_LONG).show()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtPassword)
        edtEmail.setText("")
        edtSenha.setText("")

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}