package com.example.beautytech_challenge.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beautytech_challenge.adapters.ProductAdapter
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.models.Product
import com.example.beautytech_challenge.repositories.MainRepository
import com.squareup.picasso.Picasso
import org.json.JSONObject


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
            val userInfo = JSONObject(userData)
            val clientId = userInfo.getJSONObject("usuario").getInt("id")

            Log.v("USERDATA", userInfo.toString())
            Log.v("USER_ID", clientId.toString())

            loginButton.text = "Perfil"
            loginButton.setOnClickListener {
                val profileIntent = Intent(this, ProfileActivity::class.java)
                startActivity(profileIntent)
            }

            getRecommendedProduct(clientId)
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

        val searchField = findViewById<EditText>(R.id.editTextText)
        searchField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchField.text.toString()
                Toast.makeText(this, "Pesquisa: $query", Toast.LENGTH_SHORT).show()
                true
            } else {
                false
            }
        }
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

    private fun getRecommendedProduct(clientId: Int?) {
        repository.fetchRecommendedProduct(clientId.toString()) { product, errorMessage ->
            runOnUiThread {
                if (product != null) {
                    showRecommendedProduct(product)
                } else if (errorMessage != null) {
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showRecommendedProduct(product: Product) {
        val recommendedProductCard = findViewById<CardView>(R.id.recommended_product_card)
        recommendedProductCard.visibility = View.VISIBLE

        val recommendedProductName = findViewById<TextView>(R.id.recommended_product_name)
        val recommendedProductPrice = findViewById<TextView>(R.id.recommended_product_price)
        val recommendedProductDesc = findViewById<TextView>(R.id.recommended_product_desc)
        val recommendedProductImage = findViewById<ImageView>(R.id.recommended_product_image)

        recommendedProductName.text = product.name
        recommendedProductPrice.text = product.price
        recommendedProductDesc.text = product.description ?: "Sem descrição disponível"
        Picasso.get().load(product.imageUrl).into(recommendedProductImage)
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