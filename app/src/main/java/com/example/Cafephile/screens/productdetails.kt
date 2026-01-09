package com.example.f053.screens

import com.example.f053.R
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.f053.colorsconst
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.db.CoffeeDatabase
import com.example.Cafephile.DarkModeManager

@SuppressLint("ResourceAsColor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    drinkId: Int,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val drink = CoffeeDatabase.drinks.find { it.id == drinkId } ?: return
    var selectedSize by remember { mutableStateOf("Medium") }
    var quantity by remember { mutableStateOf(1) }
    val isDarkMode by DarkModeManager.isDarkMode

    val sizes = listOf("Small", "Medium", "Large")

    val sizeMultiplier = when (selectedSize) {
        "Small" -> 0.8
        "Medium" -> 1.0
        "Large" -> 1.2
        else -> 1.0
    }

    val totalPrice = drink.price * sizeMultiplier * quantity

    val backgroundColor = if (isDarkMode) colorsconst.DarkBackground else Color(0xFFF5F5F5)
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else Color(0xFF1F2937)
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else Color(0xFF6B7280)
    val cardBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else Color(0xFF78350F)
    val bottomBarBg = if (isDarkMode) colorsconst.DarkSurface else Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = textPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { CoffeeDatabase.toggleFavorite(drinkId) }) {
                            Icon(
                                imageVector = if (drink.isFavorite)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(R.string.cd_favorite),
                                tint = if (drink.isFavorite)
                                    Color(0xFFEF4444)
                                else
                                    textSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                Surface(shadowElevation = 8.dp, color = bottomBarBg) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.label_total_price),
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                            Text(
                                text = "${String.format("%.2f", totalPrice)}DH",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }

                        Button(
                            onClick = {
                                CoffeeDatabase.addToCart(drink, selectedSize, quantity)
                                onNavigateToCart()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonBg
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_add_to_cart),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.Transparent
                    ) {
                        Image(
                            painter = painterResource(id = drink.deatilsimg),
                            contentDescription = drink.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (drink.badge != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(20.dp)
                                .background(Color(0xFFDC2626), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = drink.badge,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = drink.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = drink.description,
                        fontSize = 15.sp,
                        color = textSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.label_size),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        sizes.forEach { size ->
                            SizeChip(
                                size = size,
                                isSelected = selectedSize == size,
                                onClick = { selectedSize = size },
                                modifier = Modifier.weight(1f),
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_quantity),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (quantity > 1) buttonBg else if (isDarkMode) colorsconst.DarkBorder else Color(0xFFE5E7EB),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = stringResource(R.string.cd_decrease),
                                    tint = if (quantity > 1) Color.White else textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                modifier = Modifier.widthIn(min = 30.dp),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(buttonBg, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.cd_increase),
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = stringResource(R.string.label_product_details),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(
                                icon = Icons.Filled.LocalCafe,
                                label = stringResource(R.string.label_ingredients),
                                value = drink.ingredients,
                                isDarkMode = isDarkMode
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            DetailRow(
                                icon = Icons.Filled.LocalFireDepartment,
                                label = stringResource(R.string.label_calories),
                                value = "${drink.calories} kcal",
                                isDarkMode = isDarkMode
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            DetailRow(
                                icon = Icons.Filled.Category,
                                label = stringResource(R.string.label_category),
                                value = drink.category,
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        CoffeeFloatingToggle(onToggle = { DarkModeManager.toggleDarkMode() })
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isDarkMode: Boolean = false
) {
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else Color(0xFF1F2937)
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else Color(0xFF6B7280)
    val iconTint = if (isDarkMode) colorsconst.DarkAccent else Color(0xFFFB923C)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = textSecondary
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
        }
    }
}

@Composable
fun SizeChip(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val selectedBg = if (isDarkMode) colorsconst.DarkAccent else Color(0xFFFB923C)
    val unselectedBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textColor = if (isSelected) Color.White else if (isDarkMode) colorsconst.DarkTextPrimary else Color(0xFF1F2937)
    val subtextColor = if (isSelected) Color.White.copy(alpha = 0.8f) else if (isDarkMode) colorsconst.DarkTextSecondary else Color(0xFF9CA3AF)

    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedBg else unselectedBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = size,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = when (size) {
                        "Small" -> "8 oz"
                        "Medium" -> "12 oz"
                        "Large" -> "16 oz"
                        else -> ""
                    },
                    fontSize = 11.sp,
                    color = subtextColor
                )
            }
        }
    }
}