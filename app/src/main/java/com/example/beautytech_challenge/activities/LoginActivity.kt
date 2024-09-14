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
import com.example.beautytech_challenge.MainActivity
import com.example.beautytech_challenge.R
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : Activity() {

    val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"
    private lateinit var progressBar: ProgressBar

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login_layout)

        val cliente = OkHttpClient()
        val gson = Gson()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        progressBar = findViewById(R.id.loading_spinner)

        loginButton.setOnClickListener {
            if (edtEmail.text.isBlank() || edtSenha.text.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val jsonBody = """
                {
                    "email": "${edtEmail.text}",
                    "senha": "${edtSenha.text}"
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("$BASE_URL/login")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            progressBar.visibility = View.VISIBLE

            val response = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("LOGIN", "Credenciais Inválidas")
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "Credenciais Inválidas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                    }

                    val resposta = response.body?.string()

                    if (resposta != null) {
                        val jsonObject = JSONObject(resposta)
                        val usuario = jsonObject.getJSONObject("usuario")
                        val nomeUsuario = usuario.getString("nome")
                        val token = jsonObject.getString("token")

                        Log.d("LOGIN", "Login Efetuado com sucesso: $nomeUsuario")

                        runOnUiThread {
                            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("userData", jsonObject.toString())
                            editor.putString("token", token)
                            editor.apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Bem-vindo $nomeUsuario",
                                Toast.LENGTH_LONG
                            ).show()

                            edtEmail.setText("")
                            edtSenha.setText("")

                            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            cliente.newCall(request).enqueue(response)
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
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