package com.example.beautytech_challenge

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beautytech_challenge.activities.LoginActivity
import com.example.beautytech_challenge.activities.ProductsActivity
import com.example.beautytech_challenge.activities.ProfileActivity
import com.example.beautytech_challenge.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class MainActivity : Activity() {

    private val client = OkHttpClient()
    private val BASE_URL = "https://0f7867b6-e97c-46c8-8a0f-798b12121071-00-1xlw48mwghd1f.spock.replit.dev"
    private lateinit var productAdapter: ProductAdapter
    private lateinit var cardContainer: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.main_layout)

        progressBar = findViewById(R.id.progressBar)
        cardContainer = findViewById(R.id.cardContainer)
        cardContainer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        productAdapter = ProductAdapter(listOf()) { product ->
            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }
        cardContainer.adapter = productAdapter

        val menuProdutos = findViewById<Button>(R.id.btn_produtos)
        menuProdutos.setOnClickListener {
            val productsIntent = Intent(this, ProductsActivity::class.java)
            startActivity(productsIntent)
        }

        val btnHigiene = findViewById<Button>(R.id.btn_higiene)
        val btnCabelos = findViewById<Button>(R.id.btn_cabelos)
        val btnUnhas = findViewById<Button>(R.id.btn_unhas)
        val btnPerfumes = findViewById<Button>(R.id.btn_perfumes)

        val productIntent = Intent(this, ProductsActivity::class.java)
        btnHigiene.setOnClickListener { startActivity(productIntent) }
        btnCabelos.setOnClickListener { startActivity(productIntent) }
        btnUnhas.setOnClickListener { startActivity(productIntent) }
        btnPerfumes.setOnClickListener { startActivity(productIntent) }

        updateLoginButton()
        getProducts()
    }

    override fun onStart() {
        super.onStart()
        updateLoginButton()
    }

    private fun updateLoginButton() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userDataString = sharedPreferences.getString("userData", null)

        val loginButton: Button = findViewById(R.id.btn_login)
        if (userDataString != null) {
            loginButton.text = "Perfil"
            loginButton.setOnClickListener {
                val profileIntent = Intent(this, ProfileActivity::class.java)
                startActivity(profileIntent)
            }
        } else {
            loginButton.text = "Login"
            loginButton.setOnClickListener {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }

    private fun showProductDetails(product: Product) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.product_details_dialog_layout, null)
        dialog.setContentView(dialogView)

        val productImage: ImageView = dialogView.findViewById(R.id.dialog_product_image)
        val productName: TextView = dialogView.findViewById(R.id.dialog_product_name)
        val productPrice: TextView = dialogView.findViewById(R.id.dialog_product_price)

        productName.text = product.name
        productPrice.text = product.price
        Picasso.get().load(product.imageUrl).into(productImage)

        dialog.show()
    }

    private fun getProducts() {
        runOnUiThread { progressBar.visibility = View.VISIBLE }

        val url = "$BASE_URL/produto"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Falha ao obter os dados dos produtos", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread { progressBar.visibility = View.GONE }

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
                                productAdapter = ProductAdapter(productList) { product ->
                                    showProductDetails(product)
                                }
                                cardContainer.adapter = productAdapter
                            }
                        } catch (e: JSONException) {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Erro ao processar os dados dos produtos", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Erro ao obter os dados dos produtos", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}