package com.example.beautytech_challenge.repositories

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ProfileRepository {

    private val BASE_URL = "https://ba6cbd81-1616-4535-9d50-b84eb76f82a3-00-cbuh6miz1cm9.worf.replit.dev"
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
