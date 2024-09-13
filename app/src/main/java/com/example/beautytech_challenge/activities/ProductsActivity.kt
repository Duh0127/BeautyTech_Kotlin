package com.example.beautytech_challenge.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beautytech_challenge.ProductAdapter
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.models.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class ProductsActivity : Activity() {

    private val client = OkHttpClient()
    private val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.products_layout)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        getProducts()
    }

    private fun getProducts() {
        val url = "$BASE_URL/produto"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProductsActivity, "Falha ao obter os dados dos produtos", Toast.LENGTH_LONG).show()
                }
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

                            runOnUiThread {
                                productAdapter = ProductAdapter(productList)
                                recyclerView.adapter = productAdapter
                            }
                        } catch (e: JSONException) {
                            runOnUiThread {
                                Toast.makeText(this@ProductsActivity, "Erro ao processar os dados dos produtos", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ProductsActivity, "Erro ao obter os dados dos produtos", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
