package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.repositories.EditProfileRepository
import com.example.beautytech_challenge.repositories.ProfileRepository
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileActivity: Activity() {

    private val editProfileRepository = EditProfileRepository(activity = this@EditProfileActivity)
    private val profileRepository = ProfileRepository()
    private lateinit var userInfo: JSONObject

    private lateinit var progressBar: ProgressBar
    private lateinit var contentLayout: LinearLayout

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.edit_profile_layout)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userData = sharedPreferences.getString("userData", null)
        val userInfo = JSONObject(userData)
        val userId = userInfo.getJSONObject("usuario").getInt("id")

        progressBar = findViewById(R.id.progressBarEdit)
        contentLayout = findViewById(R.id.mainLinearLayout)

        val edtNome = findViewById<EditText>(R.id.edtNomeEdit)
        val edtEmail = findViewById<EditText>(R.id.edtEmailEdit)
        val edtCpf = findViewById<EditText>(R.id.edtCPFEdit)
        val edtDataNasc = findViewById<EditText>(R.id.edtDataNascEdit)
        val edtEstadoCivil = findViewById<EditText>(R.id.edtEstadoCivilEdit)
        val edtPele = findViewById<EditText>(R.id.edtPeleEdit)
        val edtCabelo = findViewById<EditText>(R.id.edtCabeloEdit)
        val edtSenha = findViewById<EditText>(R.id.edtSenhaEdit)
        val edtGenero = findViewById<EditText>(R.id.edtGeneroEdit)
        val edtTelefone = findViewById<EditText>(R.id.edtTelefoneEdit)


        val btnSalvar = findViewById<Button>(R.id.btnSalvarEdit)
        val btnVoltarProfile = findViewById<Button>(R.id.btnVoltarProfile)
        btnVoltarProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }


        edtCpf.filters = arrayOf(InputFilter.LengthFilter(11), InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("[0-9]*".toRegex())) null else ""
        })

        edtTelefone.filters = arrayOf(InputFilter.LengthFilter(9))

        btnSalvar.setOnClickListener {
            if (edtNome.text.isBlank() || edtEmail.text.isBlank() || edtCpf.text.isBlank() ||
                edtDataNasc.text.isBlank() || edtEstadoCivil.text.isBlank() ||
                edtTelefone.text.isBlank() || edtGenero.text.isBlank() || edtSenha.text.isBlank() ||
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

            val genero = edtGenero.text.toString().lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
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


            editProfileRepository.update(
                userId = userId.toString(),
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
    }

    override fun onStart() {
        super.onStart()

        progressBar.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("userData", null)
        val token = sharedPreferences.getString("token", null)

        if (userDataString == null || token == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userInfo = JSONObject(userDataString)
        val userId = userInfo.getJSONObject("usuario").getInt("id")

        profileRepository.getUserById(userId) { responseBody, errorMessage ->
            runOnUiThread {
                if (responseBody != null) {
                    progressBar.visibility = View.GONE
                    contentLayout.visibility = View.VISIBLE
                    Log.v("API_RESPONSE", "Dados recebidos: $responseBody")
                    Toast.makeText(this, "Dados carregados com sucesso!", Toast.LENGTH_SHORT).show()
                    preencherCamposComDados(responseBody)
                } else {
                    Toast.makeText(this, "Erro: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun preencherCamposComDados(userData: String?) {
        try {
            val userJson = JSONObject(userData)
            val nome = userJson.getString("NM_CLIENTE")
            val email = userJson.getString("EMAIL_CLIENTE")
            val cpf = userJson.getString("CPF_CLIENTE")
            val dataNascimento = userJson.getString("DT_NASCIMENTO_CLIENTE")
            val estadoCivil = userJson.getString("ESTADO_CIVIL_CLIENTE")
            val pele = userJson.getString("PELE_CLIENTE")
            val cabelo = userJson.getString("CABELO_CLIENTE")
            val senha = userJson.getString("SENHA_CLIENTE")
            val genero = userJson.getString("NM_GENERO")
            val telefone = userJson.optString("NR_TELEFONE", "Não informado")

            findViewById<EditText>(R.id.edtNomeEdit).setText(nome)
            findViewById<EditText>(R.id.edtEmailEdit).setText(email)
            findViewById<EditText>(R.id.edtCPFEdit).setText(cpf)
            findViewById<EditText>(R.id.edtDataNascEdit).setText(dataNascimento)
            findViewById<EditText>(R.id.edtEstadoCivilEdit).setText(estadoCivil)
            findViewById<EditText>(R.id.edtPeleEdit).setText(pele)
            findViewById<EditText>(R.id.edtCabeloEdit).setText(cabelo)
            findViewById<EditText>(R.id.edtSenhaEdit).setText(senha)
            findViewById<EditText>(R.id.edtGeneroEdit).setText(genero)
            findViewById<EditText>(R.id.edtTelefoneEdit).setText(telefone)
        } catch (e: JSONException) {
            Toast.makeText(this, "Erro ao processar os dados do usuário", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSuccessfulRegistration() {
        Log.d("ATUALIZACAO", "Atualização efetuada com sucesso")

        val edtNome = findViewById<EditText>(R.id.edtNomeEdit)
        val edtEmail = findViewById<EditText>(R.id.edtEmailEdit)
        val edtCpf = findViewById<EditText>(R.id.edtCPFEdit)
        val edtDataNasc = findViewById<EditText>(R.id.edtDataNascEdit)
        val edtEstadoCivil = findViewById<EditText>(R.id.edtEstadoCivilEdit)
        val edtPele = findViewById<EditText>(R.id.edtPeleEdit)
        val edtCabelo = findViewById<EditText>(R.id.edtCabeloEdit)
        val edtSenha = findViewById<EditText>(R.id.edtSenhaEdit)
        val edtGenero = findViewById<EditText>(R.id.edtGeneroEdit)
        val edtTelefone = findViewById<EditText>(R.id.edtTelefoneEdit)

        edtNome.setText("")
        edtEmail.setText("")
        edtCpf.setText("")
        edtDataNasc.setText("")
        edtEstadoCivil.setText("")
        edtPele.setText("")
        edtCabelo.setText("")
        edtTelefone.setText("")
        edtGenero.setText("")
        edtSenha.setText("")

        Toast.makeText(this, "Atualização realizada com sucesso!", Toast.LENGTH_LONG).show()

        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
        finish()
    }


}