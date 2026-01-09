package com.example.f053.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Cafephile.DarkModeManager
import com.example.f053.MainActivity
import com.example.f053.colorsconst
import com.example.f053.colorsconst.BackgroundCream
import com.example.f053.colorsconst.CoffeeDark
import com.example.f053.colorsconst.CoffeeMedium
import com.example.f053.colorsconst.Gold
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.db.AuthManager
import com.example.f053.db.CoffeeDatabase

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProfileScreen(
                onBack = { finish() },
                onNavigateToCart = {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                },
                onNavigateToFavorites = {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                },
                onLogout = { handleLogout() }
            )
        }
    }

    private fun handleLogout() {
        AuthManager.logout()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("destination", "login")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by AuthManager.currentUser
    val userName = AuthManager.getCurrentUserName()
    val usernameHandle = currentUser?.username ?: "coffee_lover"

    val loyaltyPoints = 1450
    val pointsToNextReward = 2000
    val progress = loyaltyPoints.toFloat() / pointsToNextReward.toFloat()

    val ordersCount by CoffeeDatabase.cartCount
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isDarkMode by DarkModeManager.isDarkMode

    val backgroundColor = if (isDarkMode) colorsconst.DarkBackground else BackgroundCream
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "My Profile",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    ProfileHeaderSection(userName, usernameHandle, isDarkMode)
                }

                item {
                    StatsRow(orders = ordersCount, points = loyaltyPoints, isDarkMode)
                }

                item {
                    LoyaltyProgressCard(loyaltyPoints, pointsToNextReward, progress, isDarkMode)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "General",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        color = textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val generalSettings = listOf(
                    SettingsItem(Icons.Outlined.ShoppingBag, "My Orders", "History & Reorder", onNavigateToCart),
                    SettingsItem(Icons.Outlined.FavoriteBorder, "Favorites", "Your saved drinks", onNavigateToFavorites),
                    SettingsItem(Icons.Outlined.LocationOn, "Delivery Address", "Home, Office") { },
                    SettingsItem(Icons.Outlined.CreditCard, "Payment Methods", "Visa **42") { }
                )

                items(generalSettings) { item ->
                    ProfileMenuItem(item, isDarkMode)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Support",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        color = textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                val supportSettings = listOf(
                    SettingsItem(Icons.Outlined.HelpOutline, "Help Center", "FAQ & Support") { },
                    SettingsItem(Icons.Outlined.Info, "About Us", "v1.0.0") { }
                )

                items(supportSettings) { item ->
                    ProfileMenuItem(item, isDarkMode)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Button(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEE2E2),
                                contentColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        CoffeeFloatingToggle(onToggle = { DarkModeManager.toggleDarkMode() })
    }

    if (showLogoutDialog) {
        val dialogBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
        val dialogText = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark

        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = dialogBg,
            title = { Text("Log Out", color = dialogText, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your account?", color = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = dialogText)
                }
            }
        )
    }
}

@Composable
fun ProfileHeaderSection(name: String, handle: String, isDarkMode: Boolean = false) {
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val circleBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeMedium
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark
    val borderColor = if (isDarkMode) colorsconst.DarkSurface else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .border(3.dp, borderColor, CircleShape)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 48.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = 4.dp)
                    .size(32.dp)
                    .background(buttonBg, CircleShape)
                    .border(2.dp, if (isDarkMode) colorsconst.DarkBackground else BackgroundCream, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Text(
            text = "@$handle",
            fontSize = 14.sp,
            color = textSecondary
        )
    }
}

@Composable
fun StatsRow(orders: Int, points: Int, isDarkMode: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatItem(
            count = orders.toString(),
            label = "Orders",
            modifier = Modifier.weight(1f),
            isDarkMode = isDarkMode
        )
        StatItem(
            count = points.toString(),
            label = "Points",
            modifier = Modifier.weight(1f),
            isDarkMode = isDarkMode
        )
    }
}

@Composable
fun StatItem(count: String, label: String, modifier: Modifier, isDarkMode: Boolean = false) {
    val cardBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark

    Column(
        modifier = modifier
            .background(cardBg, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray
        )
    }
}

@Composable
fun LoyaltyProgressCard(current: Int, target: Int, progress: Float, isDarkMode: Boolean = false) {
    val cardBg = if (isDarkMode) colorsconst.DarkSurface else CoffeeDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Member Gold",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$current / $target Points",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Gold,
                trackColor = Color.White.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You are ${target - current} points away from a free drink!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ProfileMenuItem(item: SettingsItem, isDarkMode: Boolean = false) {
    val iconBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val iconTint = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray.copy(alpha = 0.5f)
        )
    }
}

data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)