package com.example.f053.models

import androidx.annotation.DrawableRes

data class Drink(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    @DrawableRes val imageRes: Int,
    @DrawableRes val deatilsimg:Int,
    val badge: String? = null,
    val category: String,
    var isFavorite: Boolean = false,
    val ingredients: String = "Espresso, Milk, Sugar",
    val calories: Int = 250
)



data class CartItem(
    val drink: Drink,
    val quantity: Int = 1,
    val selectedSize: String = SizeEnum.Medium.name
) {
    val sizeMultiplier: Double
        get() = when (selectedSize) {
             SizeEnum.Small.name-> 0.8
            SizeEnum.Medium.name -> 1.0
            SizeEnum.Large.name -> 1.2
            else -> 1.0
        }

    val unitPrice: Double
        get() = drink.price * sizeMultiplier

    val subtotal: Double
        get() = unitPrice * quantity
}

data class User(
    val name: String,
    var loyaltyPoints: Int,
    val pointsToNextReward: Int
)

data class CoffeeCategory(
    val name: String, val icon: String, val colorHex: String
)

data class GalleryPhoto(
    val imageRes: Int, val username: String, val likes: Int
)

data class NearbyShop(
    val name: String, val distance: String, val lat: Double, val lng: Double
)

enum class CategoriesEnum{
    All,Espresso,Latte,Cappuccino,Cold_Brew
}

enum class SizeEnum{
    Small,Medium,Large
}

enum class badgevalues{
    Populaire,New
}