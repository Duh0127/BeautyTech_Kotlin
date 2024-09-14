package com.example.beautytech_challenge.repositories

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ProfileRepository {

    private val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"
    private val client = OkHttpClient()

    fun getUserById(userId: Int, callback: (String?, String?) -> Unit) {
        val url = "$BASE_URL/cliente/$userId"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Falha ao obter os dados do usuário")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string(), null)
                } else {
                    callback(null, "Erro ao obter os dados do usuário")
                }
            }
        })
    }
}
