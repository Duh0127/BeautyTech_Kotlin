package com.example.beautytech_challenge.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beautytech_challenge.adapters.ProductAdapter
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.models.Product
import com.example.beautytech_challenge.repositories.ProductsRepository
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException

class ProductsActivity : Activity() {

    private val repository = ProductsRepository()

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.products_layout)

        val homeButton = findViewById<Button>(R.id.btn_back_home)
        homeButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        getProducts()
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

        repository.getProducts { responseBody, errorMessage ->
            runOnUiThread {
                progressBar.visibility = View.GONE
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

                        productAdapter = ProductAdapter(productList) { product ->
                            showProductDetails(product)
                        }
                        recyclerView.adapter = productAdapter
                    } catch (e: JSONException) {
                        Toast.makeText(this@ProductsActivity, "Erro ao processar os dados dos produtos", Toast.LENGTH_LONG).show()
                    }
                } else if (errorMessage != null) {
                    Toast.makeText(this@ProductsActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
