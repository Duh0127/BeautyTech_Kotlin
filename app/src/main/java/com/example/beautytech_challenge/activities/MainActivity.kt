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
import com.example.beautytech_challenge.repositories.MainRepository
import com.squareup.picasso.Picasso


class MainActivity : Activity() {

    private val repository = MainRepository()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var cardContainer: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.main_layout)

        val loginButton = findViewById<Button>(R.id.btn_login)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userData = sharedPreferences.getString("userData", null)

        if (userData != null) {
            loginButton.text = "Perfil"
            loginButton.setOnClickListener {
                val profileIntent = Intent(this, ProfileActivity::class.java)
                startActivity(profileIntent)
            }
        } else {
            loginButton.setOnClickListener {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }

        intentsOfButtons()

        progressBar = findViewById(R.id.progressBar)
        cardContainer = findViewById(R.id.cardContainer)
        cardContainer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        productAdapter = ProductAdapter(listOf()) { product ->
            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }
        cardContainer.adapter = productAdapter

        getProducts()
    }

    private fun getProducts() {
        runOnUiThread { progressBar.visibility = View.VISIBLE }

        repository.fetchProducts { productList, errorMessage ->
            runOnUiThread {
                progressBar.visibility = View.GONE

                if (productList != null) {
                    productAdapter = ProductAdapter(productList) { product ->
                        showProductDetails(product)
                    }
                    cardContainer.adapter = productAdapter
                } else if (errorMessage != null) {
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
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

    private fun intentsOfButtons() {
        val productsButton = findViewById<Button>(R.id.btn_produtos)
        productsButton.setOnClickListener {
            val productsIntent = Intent(this, ProductsActivity::class.java)
            startActivity(productsIntent)
        }

        val btnHigiene = findViewById<Button>(R.id.btn_higiene)
        btnHigiene.setOnClickListener {
            val higieneIntent = Intent(this, ProductsActivity::class.java)
            startActivity(higieneIntent)
        }

        val btnCabelos = findViewById<Button>(R.id.btn_cabelos)
        btnCabelos.setOnClickListener {
            val cabelosIntent = Intent(this, ProductsActivity::class.java)
            startActivity(cabelosIntent)
        }

        val btnUnhas = findViewById<Button>(R.id.btn_unhas)
        btnUnhas.setOnClickListener {
            val unhasIntent = Intent(this, ProductsActivity::class.java)
            startActivity(unhasIntent)
        }

        val btnPerfumes = findViewById<Button>(R.id.btn_perfumes)
        btnPerfumes.setOnClickListener {
            val perfumesIntent = Intent(this, ProductsActivity::class.java)
            startActivity(perfumesIntent)
        }
    }
}