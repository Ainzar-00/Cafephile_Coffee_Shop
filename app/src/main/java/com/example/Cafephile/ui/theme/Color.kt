package com.example.f053.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.f053.R

// Helper function to get Color from resource
@Composable
fun colorRes(id: Int): Color {
    return Color(ContextCompat.getColor(LocalContext.current, id))
}

// Coffee colors
@Composable
fun CoffeeDark() = colorRes(R.color.coffee_dark)

@Composable
fun CoffeeMedium() = colorRes(R.color.coffee_medium)

@Composable
fun CoffeeAccent() = colorRes(R.color.coffee_accent)

// Background
@Composable
fun BackgroundCream() = colorRes(R.color.background_cream)

// Other colors
@Composable
fun RatingYellow() = colorRes(R.color.rating_yellow)

@Composable
fun Gold() = colorRes(R.color.gold)

