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

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

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

        val deleteConfirmationModal = findViewById<LinearLayout>(R.id.modal_delete_confirmation)
        val btnCancel = findViewById<Button>(R.id.btnCancelDelete)
        val btnConfirmDelete = findViewById<Button>(R.id.btnConfirmDelete)

        findViewById<Button>(R.id.btnDeleteProfile).setOnClickListener {
            deleteConfirmationModal.visibility = View.VISIBLE
            profileContent.visibility = View.GONE
        }

        btnCancel.setOnClickListener {
            deleteConfirmationModal.visibility = View.GONE
            profileContent.visibility = View.VISIBLE
        }

        btnConfirmDelete.setOnClickListener {
            deleteConfirmationModal.visibility = View.GONE
            val userId = userInfo.getJSONObject("usuario").getInt("id")
            deleteUserById(userId)
            profileContent.visibility = View.GONE
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
                        val statusCivilView = findViewById<TextView>(R.id.edtEstadoCivilProfile)
                        val generoView = findViewById<TextView>(R.id.edtGeneroProfile)
                        val cabeloView = findViewById<TextView>(R.id.edtCabeloProfile)
                        val peleView = findViewById<TextView>(R.id.edtPeleProfile)

                        nomeTextView.text = nomeUsuario
                        emailTextView.text = emailUsuario
                        statusCivilView.text = statusCivil
                        generoView.text = genero
                        cabeloView.text = cabelo
                        peleView.text = pele

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

    private fun deleteUserById(userId: Int) {
        runOnUiThread { progressBar.visibility = View.VISIBLE }

        repository.deleteProfile(userId) { responseBody, errorMessage ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (responseBody != null) {
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    Toast.makeText(this@ProfileActivity, "Perfil excluído com sucesso", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else if (errorMessage != null) {
                    Toast.makeText(this@ProfileActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

