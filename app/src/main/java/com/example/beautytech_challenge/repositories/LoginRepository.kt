package com.example.beautytech_challenge.repositories

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginRepository () {

    private val client = OkHttpClient()
    private val BASE_URL = "https://ba6cbd81-1616-4535-9d50-b84eb76f82a3-00-cbuh6miz1cm9.worf.replit.dev"

    fun login(email: String, password: String, callback: (JSONObject?, String?) -> Unit) {
        val jsonBody = """
            {
                "email": "$email",
                "senha": "$password"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/login")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Falha ao conectar ao servidor")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            callback(jsonObject, null)
                        } catch (e: Exception) {
                            callback(null, "Erro ao processar a resposta do servidor")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Credenciais inválidas")
                }
            }
        })
    }
}
