package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
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
        val edtTelefone = findViewById<EditText>(R.id.edtTelefone)

        edtCpf.filters = arrayOf(InputFilter.LengthFilter(11), InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("[0-9]*".toRegex())) null else ""
        })

        edtTelefone.filters = arrayOf(InputFilter.LengthFilter(9))

        val edtGen = findViewById<EditText>(R.id.edtGenero)
        val edtPele = findViewById<EditText>(R.id.edtPele)
        val edtCabelo = findViewById<EditText>(R.id.edtCabelo)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        val btnCadastro = findViewById<Button>(R.id.btnRegister)
        btnCadastro.setOnClickListener {
            if (edtNome.text.isBlank() || edtEmail.text.isBlank() || edtCpf.text.isBlank() ||
                edtDataNasc.text.isBlank() || edtEstadoCivil.text.isBlank() ||
                edtTelefone.text.isBlank() || edtGen.text.isBlank() || edtSenha.text.isBlank() ||
                edtPele.text.isBlank() || edtCabelo.text.isBlank()
            ) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val cabelo = edtCabelo.text.toString().lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
            if (cabelo !in listOf("Liso", "Ondulado", "Cacheado", "Crespo")) {
                Toast.makeText(this, "Tipo de cabelo inválido. Use: Liso, Ondulado, Cacheado ou Crespo.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val pele = edtPele.text.toString().lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
            if (pele !in listOf("Branca", "Parda", "Negra")) {
                Toast.makeText(this, "Tipo de pele inválido. Use: Branca, Parda ou Negra.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val genero = edtGen.text.toString().lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
            if (genero !in listOf("Masculino", "Feminino")) {
                Toast.makeText(this, "Gênero inválido. Use Masculino ou Feminino.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val estadoCivil = edtEstadoCivil.text.toString().uppercase(Locale.getDefault())
            if (estadoCivil !in listOf("CASADO", "SOLTEIRO")) {
                Toast.makeText(this, "Estado Civil inválido. Use CASADO ou SOLTEIRO.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val telefone = edtTelefone.text.toString()
                if (telefone.length != 9) {
                    throw IllegalArgumentException("O telefone deve conter exatamente 9 caracteres.")
                }
                telefone.toIntOrNull() ?: throw NumberFormatException("O telefone deve conter apenas números.")
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } catch (e: NumberFormatException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            val idGenero = when {
                genero == "Masculino" -> 1
                genero == "Feminino" -> 2
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

            registerRepository.register(
                nome = edtNome.text.toString(),
                email = edtEmail.text.toString(),
                cpf = edtCpf.text.toString(),
                dataNascimento = dataNascimento,
                estadoCivil = estadoCivil,
                pele = pele,
                cabelo = cabelo,
                senha = edtSenha.text.toString(),
                ddi = "+55",
                ddd = 11,
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
        val edtPele = findViewById<EditText>(R.id.edtPele)
        val edtCabelo = findViewById<EditText>(R.id.edtCabelo)
        val edtTelefone = findViewById<EditText>(R.id.edtTelefone)
        val edtGen = findViewById<EditText>(R.id.edtGenero)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)

        edtNome.setText("")
        edtEmail.setText("")
        edtCpf.setText("")
        edtDataNasc.setText("")
        edtEstadoCivil.setText("")
        edtPele.setText("")
        edtCabelo.setText("")
        edtTelefone.setText("")
        edtGen.setText("")
        edtSenha.setText("")

        Toast.makeText(this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_LONG).show()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}