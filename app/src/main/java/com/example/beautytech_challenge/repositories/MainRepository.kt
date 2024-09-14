package com.example.beautytech_challenge.repositories

import com.example.beautytech_challenge.models.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class MainRepository () {

    private val client = OkHttpClient()
    private val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"


    fun fetchProducts(callback: (List<Product>?, String?) -> Unit) {
        val url = "$BASE_URL/produto"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Falha ao obter os dados dos produtos")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonArray = JSONArray(responseBody)
                            val productList = mutableListOf<Product>()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val product = Product(
                                    id = jsonObject.getInt("ID_PRODUTO"),
                                    name = jsonObject.getString("NM_PRODUTO"),
                                    price = "R$${jsonObject.getDouble("VL_PRODUTO")}",
                                    imageUrl = jsonObject.getString("IMG_PRODUTO")
                                )
                                productList.add(product)
                            }

                            callback(productList, null)
                        } catch (e: JSONException) {
                            callback(null, "Erro ao processar os dados dos produtos")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Erro ao obter os dados dos produtos")
                }
            }
        })
    }










}