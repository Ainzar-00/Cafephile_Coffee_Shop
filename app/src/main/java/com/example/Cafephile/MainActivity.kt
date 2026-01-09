package com.example.f053

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import com.example.Cafephile.DarkModeManager
import com.example.f053.screens.*
import com.example.f053.db.AuthManager
import com.example.f053.models.CategoriesEnum
import com.example.myapplication.SplashScreenActivity1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthManager.init(this)
        DarkModeManager.init(this)
        AnimationManager.init(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val isLoggedIn by AuthManager.isLoggedIn

                    val fromAnimation = intent.getBooleanExtra("from_animation", false)

                    val startDestination = when {
                        !isLoggedIn -> "login"
                        fromAnimation -> "home"
                        AnimationManager.shouldShowAnimation() -> {
                            LaunchedEffect(Unit) {
                                val intent = Intent(this@MainActivity, SplashScreenActivity1::class.java)
                                startActivity(intent)
                                finish()
                            }
                            "home"
                        }
                        else -> "home"
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    if (AnimationManager.shouldShowAnimation()) {
                                        val intent = Intent(this@MainActivity,
                                            SplashScreenActivity1::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToExplore = {
                                    startActivity(Intent(this@MainActivity, ExploreActivity::class.java))
                                },
                                onNavigateToCart = {
                                    startActivity(Intent(this@MainActivity, CartActivity::class.java))
                                },
                                onNavigateToFavorites = {
                                    startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
                                },
                                onNavigateToProfile = {
                                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                                },
                                onNavigateToDetails = { drinkId ->
                                    navController.navigate("details/$drinkId")
                                },
                                { AppNavigator.openProducts(this@MainActivity, CategoriesEnum.All.name) }
                            )
                        }

                        composable("details/{drinkId}") { backStackEntry ->
                            val drinkId = backStackEntry.arguments?.getString("drinkId")?.toIntOrNull()
                            drinkId?.let {
                                ProductDetailsScreen(
                                    drinkId = it,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToCart = {
                                        startActivity(Intent(this@MainActivity, CartActivity::class.java))
                                    }
                                )
                            }
                        }

                        composable("explore") {
                            LaunchedEffect(Unit) {
                                startActivity(Intent(this@MainActivity, ExploreActivity::class.java))
                            }
                        }
                    }
                }
            }
        }
    }
}