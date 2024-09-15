package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.repositories.ProfileRepository
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : Activity() {

    private lateinit var userInfo: JSONObject
    private val repository = ProfileRepository()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.profile_layout)

        val profileContent = findViewById<LinearLayout>(R.id.profile_content)
        progressBar = findViewById(R.id.loading_spinner)

        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            Toast.makeText(this@ProfileActivity, "Até mais...", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileContent.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("userData", null)
        val token = sharedPreferences.getString("token", null)

        if (userDataString == null || token == null) {
            Toast.makeText(this@ProfileActivity, "Usuário não autenticado", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        userInfo = JSONObject(userDataString)
        val userId = userInfo.getJSONObject("usuario").getInt("id")

        getUserById(userId)
    }

    private fun getUserById(userId: Int) {
        runOnUiThread { progressBar.visibility = View.VISIBLE }

        repository.getUserById(userId) { responseBody, errorMessage ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        Log.d("USER", jsonObject.toString())

                        val nomeUsuario = jsonObject.getString("NM_CLIENTE")
                        val emailUsuario = jsonObject.getString("EMAIL_CLIENTE")
                        val statusCivil = jsonObject.getString("ESTADO_CIVIL_CLIENTE")
                        val genero = jsonObject.getString("NM_GENERO")
                        val cabelo = jsonObject.getString("CABELO_CLIENTE")
                        val pele = jsonObject.getString("PELE_CLIENTE")
                        val cpf = jsonObject.getString("CPF_CLIENTE")
                        val nrtelefone = jsonObject.getString("NR_TELEFONE")
                        val ddd = jsonObject.getString("DDD_TELEFONE")

                        val nomeTextView = findViewById<TextView>(R.id.txtName)
                        val emailTextView = findViewById<TextView>(R.id.txtEmail)
                        val statusCivilView = findViewById<TextView>(R.id.txtCabelo)
                        val generoView = findViewById<TextView>(R.id.txtPele)
                        val cabeloView = findViewById<TextView>(R.id.txtCabelo)
                        val peleView = findViewById<TextView>(R.id.txtPele)
                        val cpfView = findViewById<TextView>(R.id.txtCpf)
                        val telView = findViewById<TextView>(R.id.txtTelefone)

                        nomeTextView.text = nomeUsuario
                        emailTextView.text = emailUsuario
                        statusCivilView.text = statusCivil
                        generoView.text = genero
                        cabeloView.text = cabelo
                        peleView.text = pele
                        cpfView.text = """CPF $cpf"""
                        telView.text = """($ddd) $nrtelefone"""

                        findViewById<LinearLayout>(R.id.profile_content).visibility = View.VISIBLE
                    } catch (e: JSONException) {
                        Toast.makeText(this@ProfileActivity, "Erro ao processar os dados do usuário", Toast.LENGTH_LONG).show()
                    }
                } else if (errorMessage != null) {
                    Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

