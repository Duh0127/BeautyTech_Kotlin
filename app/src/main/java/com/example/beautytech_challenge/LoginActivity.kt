package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivity : Activity() {

    val BASE_URL = ""

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login_activity)

        val cliente = OkHttpClient()
        val gson = Gson()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)

        loginButton.setOnClickListener {
            val jsonBody = """
            {
                "email": "${edtEmail.text}",
                "password": "${edtSenha.text}"
            }
            """.trimIndent()

            Toast.makeText(
                this@LoginActivity,
                "JSON -> $jsonBody",
                Toast.LENGTH_LONG).show()

            // Limpa os campos de entrada
            edtEmail.setText("")
            edtSenha.setText("")


//            val request = Request.Builder()
//                .url("$BASE_URL/contatos.json")
//                .post(jsonBody.toRequestBody(
//                    "application/json".toMediaType()
//                ))
//                .build()

//            val response = object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    TODO("Not yet implemented")
//                }
//                override fun onResponse(call: Call, response: Response) {
//                    val resposta = response.body?.string()
//                    Log.d("AGENDA", "Resposta: $resposta")
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "Contato gravado $resposta",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }
//            cliente.newCall(request).enqueue(response)
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