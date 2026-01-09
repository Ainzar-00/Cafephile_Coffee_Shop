package com.example.f053.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class ProductDetailsWrapperActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drinkId = intent.getIntExtra("drinkId", 0)

        setContent {
            ProductDetailsScreen(
                drinkId = drinkId,
                onBack = { finish() },
                onNavigateToCart = {
                    startActivity(Intent(this, CartActivity::class.java))

                }
            )
        }
    }
}