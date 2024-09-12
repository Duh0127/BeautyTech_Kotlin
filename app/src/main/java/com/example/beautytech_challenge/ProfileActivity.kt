package com.example.beautytech_challenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class ProfileActivity : Activity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.profile_layout)

        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }


}