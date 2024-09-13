package com.example.beautytech_challenge.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.beautytech_challenge.MainActivity
import com.example.beautytech_challenge.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : Activity() {

    lateinit var userInfo: JSONObject
    val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"
    val cliente = OkHttpClient()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.profile_layout)

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
        val url = "$BASE_URL/cliente/$userId"
        val request = Request.Builder()
            .url(url)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Falha ao obter os dados do usuário", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            Log.d("USER", jsonObject.toString())

                            val nomeUsuario = jsonObject.getString("NM_CLIENTE")
                            val emailUsuario = jsonObject.getString("EMAIL_CLIENTE")
                            val statusCivil = jsonObject.getString("ESTADO_CIVIL_CLIENTE")
                            val genero = jsonObject.getString("NM_GENERO")
                            val cpf = jsonObject.getString("CPF_CLIENTE")
                            val nrtelefone = jsonObject.getString("NR_TELEFONE")
                            val ddd = jsonObject.getString("DDD_TELEFONE")

                            runOnUiThread {
                                val nomeTextView = findViewById<TextView>(R.id.txtName)
                                val emailTextView = findViewById<TextView>(R.id.txtEmail)
                                val statusCivilView = findViewById<TextView>(R.id.txtStatusCivil)
                                val generoView = findViewById<TextView>(R.id.txtGenero)
                                val cpfView = findViewById<TextView>(R.id.txtCpf)
                                val telView = findViewById<TextView>(R.id.txtTelefone)

                                nomeTextView.text = nomeUsuario
                                emailTextView.text = emailUsuario
                                statusCivilView.text = statusCivil
                                generoView.text = genero
                                cpfView.text = """CPF $cpf"""
                                telView.text = """($ddd) $nrtelefone"""
                            }
                        } catch (e: JSONException) {
                            runOnUiThread {
                                Toast.makeText(this@ProfileActivity, "Erro ao processar os dados do usuário", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Erro ao obter os dados do usuário", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}

