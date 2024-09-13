package com.example.beautytech_challenge.activities

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beautytech_challenge.ProductAdapter
import com.example.beautytech_challenge.R
import com.example.beautytech_challenge.models.Product

class ProductsActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.products_layout)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data for demonstration
        val productList = listOf(
            Product(12, "R$100,00", "https://example.com/image1.jpg"),
            Product(17, "R$200,00", "https://example.com/image2.jpg")
            // Add more products as needed
        )

        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter
    }
}
