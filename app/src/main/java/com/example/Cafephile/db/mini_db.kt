package com.example.f053.db

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.f053.models.Drink
import com.example.f053.R
import com.example.f053.models.CartItem
import com.example.f053.models.CategoriesEnum
import com.example.f053.models.CoffeeCategory
import com.example.f053.models.SizeEnum
import com.example.f053.models.badgevalues


object CoffeeDatabase {



    val categories = listOf(
        CoffeeCategory(CategoriesEnum.Espresso.name, "☕", "#8B4513"),
        CoffeeCategory(CategoriesEnum.Latte.name, "🥛", "#D2B48C"),
        CoffeeCategory(CategoriesEnum.Cappuccino.name, "☕", "#A0826D"),
        CoffeeCategory(CategoriesEnum.Cold_Brew.name, "🧊", "#4A90A4"),
    )

    val chipscategories = listOf(
        CategoriesEnum.All.name,
        CategoriesEnum.Espresso.name,
        CategoriesEnum.Latte.name,
        CategoriesEnum.Cappuccino.name,
        CategoriesEnum.Cold_Brew.name
    )

    val drinks = mutableStateListOf(
        Drink(1, "Caramel Macchiato", "Espresso, steamed milk, vanilla",
            25.20, 30.25,
            imageRes=R.drawable.drink_caramel_macchiato, deatilsimg = R.drawable.drink_caramel_macchiato2 ,"20% OFF", CategoriesEnum.Espresso.name),

    Drink(id = 2, name = "Bold Espresso", description = "A strong and rich coffee made by forcing hot water through finely-ground coffee beans.", price = 20.50, originalPrice = 25.3, imageRes = R.drawable.espresso_1_1, deatilsimg = R.drawable.espresso_1_2,
    badge = badgevalues.New.name,
    category = CategoriesEnum.Espresso.name,
    isFavorite = false,
    ingredients = "Finely ground coffee beans, Water",
    calories = 5
    ),

       Drink(
            id =3,
    name = "Reishi Latte",
    description = "A calming latte infused with reishi mushroom, known for its earthy flavor and wellness benefits.",
    price = 4.80,
    originalPrice = null,
    imageRes = R.drawable.reishi_latte_1_1,
    deatilsimg = R.drawable.reishi_latte_1_2,
    badge = badgevalues.Populaire.name,
    category = CategoriesEnum.Latte.name,
    isFavorite = false,
    ingredients = "Reishi mushroom extract, Espresso, Steamed milk, Natural sweetener",
    calories = 180
    ),
      Drink(
            id = 4,
    name = "Cappuccino",
    description = "A classic Italian coffee made with equal parts espresso, steamed milk, and rich milk foam.",
    price = 30.90,
    originalPrice = 40.0,
    imageRes = R.drawable.cappucio_1_1,
    deatilsimg = R.drawable.cappucino_1_2,
    badge = badgevalues.Populaire.name,
    category = CategoriesEnum.Cappuccino.name,
    isFavorite = false,
    ingredients = "Espresso, Steamed milk, Milk foam",
    calories = 150
    )




    )

    val cartItems = mutableStateListOf<CartItem>()

    var cartCount = mutableStateOf(0)
        private set

    fun getDrinksByCategory(category: String): List<Drink> {
        return if (category == CategoriesEnum.All.name) drinks else drinks.filter { it.category == category }
    }

    fun toggleFavorite(drinkId: Int) {
        val index = drinks.indexOfFirst { it.id == drinkId }
        if (index != -1) {
            drinks[index] = drinks[index].copy(isFavorite = !drinks[index].isFavorite)
        }
    }

    fun addToCart(drink: Drink, size: String = SizeEnum.Medium.name, quantity: Int = 1) {
        val existingIndex = cartItems.indexOfFirst {
            it.drink.id == drink.id && it.selectedSize == size
        }

        if (existingIndex != -1) {
            val existingItem = cartItems[existingIndex]
            cartItems[existingIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            cartItems.add(CartItem(drink, quantity, size))
        }

        updateCartCount()
    }

    fun removeFromCart(cartItem: CartItem) {
        cartItems.remove(cartItem)
        updateCartCount()
    }

    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            val index = cartItems.indexOfFirst {
                it.drink.id == cartItem.drink.id && it.selectedSize == cartItem.selectedSize
            }
            if (index != -1) {
                cartItems[index] = cartItem.copy(quantity = newQuantity)
                updateCartCount()
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
        updateCartCount()
    }

    fun getCartTotal(): Double {
        return cartItems.sumOf { it.subtotal }
    }

    private fun updateCartCount() {
        cartCount.value = cartItems.sumOf { it.quantity }
    }


}
