package com.example.beautytech_challenge.repositories

import android.app.Activity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.beautytech_challenge.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class EditProfileRepository(private val activity: Activity) {

    private val client = OkHttpClient()
    private val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"

    fun update(
        userId: String,
        nome: String,
        email: String,
        cpf: String,
        dataNascimento: String,
        estadoCivil: String,
        pele: String,
        cabelo: String,
        senha: String,
        ddi: String,
        ddd: Int,
        telefone: String,
        idGenero: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val jsonBody = """
            {
                "nome": "$nome",
                "email": "$email",
                "cpf": "$cpf",
                "dataNascimento": "$dataNascimento",
                "estadoCivil": "$estadoCivil",
                "pele": "$pele",
                "cabelo": "$cabelo",
                "senha": "$senha",
                "ddi": "$ddi",
                "ddd": "$ddd",
                "telefone": "$telefone",
                "id_genero": "$idGenero"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/cliente/${userId}")
            .put(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Informações inválidas")
                }
            }
        })
    }
}
