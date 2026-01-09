package com.example.f053.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.f053.MainActivity
import com.example.f053.R
import com.example.f053.colorsconst
import com.example.f053.colorsconst.BackgroundCream
import com.example.f053.colorsconst.CoffeeDark
import com.example.f053.colorsconst.CoffeeMedium
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.db.CoffeeDatabase
import com.example.Cafephile.DarkModeManager
import com.example.f053.models.Drink

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "favorites"
            ) {
                composable("favorites") {
                    FavoritesScreenContent(
                        onBack = { finish() },
                        onNavigateToHome = {
                            val intent = Intent(this@FavoritesActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                            finish()
                        },
                        onNavigateToProductDetails = { drinkId ->
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
                            startActivity(Intent(this@FavoritesActivity, CartActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProductDetails: (Int) -> Unit
) {
    val favoriteDrinks = CoffeeDatabase.drinks.filter { it.isFavorite }
    val isDarkMode by DarkModeManager.isDarkMode

    val backgroundColor = if (isDarkMode) colorsconst.DarkBackground else BackgroundCream
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.title_favorites),
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.nav_back),
                                tint = textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            }
        ) { padding ->
            if (favoriteDrinks.isEmpty()) {
                EmptyFavoritesView(onBrowse = onNavigateToHome, isDarkMode = isDarkMode)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteDrinks) { drink ->
                        FavoriteCoffeeCard(
                            drink = drink,
                            onNavigate = { onNavigateToProductDetails(drink.id) },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }

        CoffeeFloatingToggle(onToggle = { DarkModeManager.toggleDarkMode() })
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun FavoriteCoffeeCard(drink: Drink, onNavigate: () -> Unit, isDarkMode: Boolean = false) {
    var isFav by remember { mutableStateOf(drink.isFavorite) }

    val cardBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val imageBg = if (isDarkMode) colorsconst.DarkSurface else BackgroundCream
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(imageBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = drink.imageRes),
                    contentDescription = drink.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = {
                        drink.isFavorite = !drink.isFavorite
                        isFav = drink.isFavorite
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = drink.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = textPrimary,
                maxLines = 1
            )

            Text(
                text = drink.category,
                fontSize = 12.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%.2f", drink.price)}DH",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = textPrimary
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(buttonBg, CircleShape)
                        .clickable { CoffeeDatabase.addToCart(drink) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesView(onBrowse: () -> Unit, isDarkMode: Boolean = false) {
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "☕", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(R.string.title_favorites),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Text(
            stringResource(R.string.empty_favorites_message),
            fontSize = 14.sp,
            color = textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBrowse,
            colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.action_go_to_menu))
        }
    }
}