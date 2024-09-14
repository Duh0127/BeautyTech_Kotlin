package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.repositories.RegisterRepository
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

    private val registerRepository = RegisterRepository()

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

            registerRepository.register(
                nome = edtNome.text.toString(),
                email = edtEmail.text.toString(),
                cpf = edtCpf.text.toString(),
                dataNascimento = dataNascimento,
                estadoCivil = edtEstadoCivil.text.toString(),
                senha = edtSenha.text.toString(),
                ddi = "+55",
                ddd = ddd,
                telefone = edtTelefone.text.toString(),
                idGenero = idGenero
            ) { success, errorMessage ->
                runOnUiThread {
                    if (success) {
                        handleSuccessfulRegistration()
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleSuccessfulRegistration() {
        Log.d("CADASTRO", "Cadastro Efetuado com sucesso")

        val edtNome = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtCpf = findViewById<EditText>(R.id.edtCpf)
        val edtDataNasc = findViewById<EditText>(R.id.edtDtNasc)
        val edtEstadoCivil = findViewById<EditText>(R.id.edtEstadoCivil)
        val edtDDD = findViewById<EditText>(R.id.edtDDD)
        val edtTelefone = findViewById<EditText>(R.id.edtTelefone)
        val edtGen = findViewById<EditText>(R.id.edtGenero)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        edtNome.setText("")
        edtEmail.setText("")
        edtCpf.setText("")
        edtDataNasc.setText("")
        edtEstadoCivil.setText("")
        edtDDD.setText("")
        edtTelefone.setText("")
        edtGen.setText("")
        edtSenha.setText("")

        Toast.makeText(this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_LONG).show()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}