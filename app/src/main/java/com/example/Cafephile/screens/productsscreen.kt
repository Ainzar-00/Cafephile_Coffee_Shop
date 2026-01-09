package com.example.f053.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.Cafephile.DarkModeManager
import com.example.f053.R
import com.example.f053.colorsconst.CoffeeDark
import com.example.f053.db.CoffeeDatabase
import com.example.f053.models.Drink

class ProductsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val incomingSelected = intent?.getStringExtra("selected_category") ?: "All"
        val incomingCategories = intent?.getStringArrayListExtra("categories") ?: arrayListOf()

        val spinnerCategories = ArrayList<String>().apply {
            add("All")
            if (incomingCategories.isNotEmpty()) {
                addAll(incomingCategories.filter { it.isNotBlank() })
            }
        }

        Log.d("ProductsActivity", "incomingSelected=$incomingSelected")
        Log.d("ProductsActivity", "spinnerCategories=$spinnerCategories")

        setContent {
            val navController = rememberNavController()

            MaterialTheme {
                NavHost(
                    navController = navController,
                    startDestination = "products"
                ) {
                    composable("products") {
                        ProductsScreen(
                            categories = spinnerCategories,
                            initialCategory = incomingSelected,
                            onBack = { finish() },
                            onProductClick = { drinkId ->
                                navController.navigate("details/$drinkId")
                            }
                        )
                    }

                    composable(
                        route = "details/{drinkId}",
                        arguments = listOf(navArgument("drinkId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val drinkId = backStackEntry.arguments?.getInt("drinkId") ?: 0
                        ProductDetailsScreen(
                            drinkId = drinkId,
                            onBack = { navController.popBackStack() },
                            onNavigateToCart = {
                                startActivity(Intent(this@ProductsActivity, CartActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsScreen(
    categories: List<String>,
    initialCategory: String = "All",
    onBack: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val isDarkMode by DarkModeManager.isDarkMode
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val allDrinks = CoffeeDatabase.drinks

    val filteredDrinks by remember(query, selectedCategory, allDrinks) {
        derivedStateOf {
            allDrinks.filter { d ->
                val matchesCategory = selectedCategory.equals("All", ignoreCase = true) ||
                        d.category.equals(selectedCategory, ignoreCase = true) ||
                        d.category.contains(selectedCategory, ignoreCase = true) ||
                        selectedCategory.contains(d.category, ignoreCase = true)

                val matchesQuery = query.text.isBlank() ||
                        d.name.contains(query.text, ignoreCase = true) ||
                        d.description.contains(query.text, ignoreCase = true)

                matchesCategory && matchesQuery
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Our Products", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                },
                backgroundColor = Color.White,
                contentColor = CoffeeDark,
                elevation = 2.dp
            )
        },
        backgroundColor = Color(0xFFF9F4EF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search products...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
                    }
                )
            }

            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
                    AndroidView(factory = { ctx ->
                        Spinner(ctx).apply {
                            adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, categories).also { a ->
                                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }

                            val initIndex = categories.indexOfFirst { it.equals(initialCategory, ignoreCase = true) }
                                .takeIf { it >= 0 } ?: 0

                            setSelection(initIndex)

                            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    val name = categories[position]
                                    post { if (selectedCategory != name) selectedCategory = name }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) { /* no-op */ }
                            }
                        }
                    }, update = { spinner ->
                        val idx = categories.indexOfFirst { it.equals(selectedCategory, ignoreCase = true) }
                            .takeIf { it >= 0 } ?: 0
                        if (spinner.selectedItemPosition != idx) spinner.setSelection(idx)
                    }, modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Popular", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
                    Text(text = "${filteredDrinks.size} items", color = Color.Gray, fontSize = 14.sp)
                }
            }

            item {
                PopularItemsGrid(
                    drinks = filteredDrinks,
                    onClick = onProductClick,
                    isDarkMode = isDarkMode
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}