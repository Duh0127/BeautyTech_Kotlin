package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.beautytech_challenge.R
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class RegisterActivity : Activity() {
    val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"
    val cliente = OkHttpClient()
    val gson = Gson()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.register_layout)

        val edtNome = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtCpf = findViewById<EditText>(R.id.edtCpf)
        val edtDataNasc = findViewById<EditText>(R.id.edtDtNasc)
        val edtEstadoCivil = findViewById<EditText>(R.id.edtEstadoCivil)
        val edtDDD = findViewById<EditText>(R.id.edtDDD)
        val edtTelefone = findViewById<EditText>(R.id.edtTelefone)
        val edtGen = findViewById<EditText>(R.id.edtGenero)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        val btnCadastro = findViewById<Button>(R.id.btnRegister)
        btnCadastro.setOnClickListener {
            if (edtNome.text.isBlank() || edtEmail.text.isBlank() || edtCpf.text.isBlank() ||
                edtDataNasc.text.isBlank() || edtEstadoCivil.text.isBlank() || edtDDD.text.isBlank() ||
                edtTelefone.text.isBlank() || edtGen.text.isBlank() || edtSenha.text.isBlank()
            ) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idGenero = when {
                edtGen.text.contains("Masculino", ignoreCase = true) -> 1
                edtGen.text.contains("Feminino", ignoreCase = true) -> 2
                else -> {
                    Toast.makeText(this, "Gênero inválido. Use Masculino ou Feminino.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dataNascimento: String
            try {
                val date = inputDateFormat.parse(edtDataNasc.text.toString())
                dataNascimento = outputDateFormat.format(date)
            } catch (e: Exception) {
                Toast.makeText(this, "Data de nascimento inválida. Use o formato dd/MM/yyyy.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val ddd: Int
            try {
                ddd = edtDDD.text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "DDD inválido. Deve conter apenas números.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val jsonBody = """
                {
                    "nome": "${edtNome.text}",
                    "email": "${edtEmail.text}",
                    "cpf": "${edtCpf.text}",
                    "dataNascimento": "${dataNascimento}",
                    "estadoCivil": "${edtEstadoCivil.text}",
                    "senha": "${edtSenha.text}",
                    "ddi": "+55",
                    "ddd": "${ddd}",
                    "telefone": "${edtTelefone.text}",
                    "id_genero": "${idGenero}"
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("$BASE_URL/cliente")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("CADASTRO", "Informações Inválidas")
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Informações Inválidas",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("CADASTRO", "Cadastro Efetuado com sucesso")

                    edtNome.setText("")
                    edtEmail.setText("")
                    edtCpf.setText("")
                    edtDataNasc.setText("")
                    edtEstadoCivil.setText("")
                    edtDDD.setText("")
                    edtTelefone.setText("")
                    edtGen.setText("")
                    edtSenha.setText("")

                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Cadastro Realizado com Sucesso!",
                            Toast.LENGTH_LONG
                        ).show()

                        val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }
                }
            }
            cliente.newCall(request).enqueue(response)
        }


        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}