package com.example.beautytech_challenge.repositories

import android.util.Log
import com.example.beautytech_challenge.models.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainRepository () {

    private val client = OkHttpClient()
    private val BASE_URL = "https://ba6cbd81-1616-4535-9d50-b84eb76f82a3-00-cbuh6miz1cm9.worf.replit.dev"

    private val IA_BASE_URL = "https://40b3ffcb-33fd-454d-9c85-a2d624cd785e-00-3mn86mnbqxlt6.janeway.replit.dev/recommend"

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
                                    imageUrl = jsonObject.getString("IMG_PRODUTO"),
                                    description = jsonObject.getString("DESC_PRODUTO")
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

    fun fetchRecommendedProduct(clientId: String, callback: (Product?, String?) -> Unit) {
        val urlWithClientId = "$IA_BASE_URL?client_id=$clientId"
        val request = Request.Builder()
            .url(urlWithClientId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Falha ao obter recomendação de produto")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody)
                            val productDetails = jsonObject.getJSONObject("details")
                            val product = Product(
                                id = productDetails.getInt("ID_PRODUTO"),
                                name = productDetails.getString("NM_PRODUTO"),
                                price = "R$${productDetails.getDouble("VL_PRODUTO")}",
                                imageUrl = productDetails.getString("IMG_PRODUTO"),
                                description = productDetails.getString("DESC_PRODUTO")
                            )

                            callback(product, null)
                        } catch (e: JSONException) {
                            callback(null, "Erro ao processar recomendação")
                        }
                    } else {
                        callback(null, "Resposta vazia do servidor")
                    }
                } else {
                    callback(null, "Erro ao obter recomendação de produto")
                }
            }
        })
    }

}