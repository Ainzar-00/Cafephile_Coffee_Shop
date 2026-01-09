package com.example.f053.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f053.colorsconst
import com.example.Cafephile.DarkModeManager
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.db.AuthManager
import com.example.f053.db.CoffeeDatabase
import com.example.f053.models.Drink
import com.example.f053.R
import com.example.f053.models.badgevalues

@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    openProducts:()->Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val cartCount by CoffeeDatabase.cartCount

    val isDarkMode by DarkModeManager.isDarkMode

    val backgroundColor = if (isDarkMode) colorsconst.DarkBackground else colorsconst.BackgroundCream
    val cardColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else colorsconst.CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else colorsconst.CoffeeMedium

    val allDrinks = CoffeeDatabase.getDrinksByCategory(selectedCategory)
    val filteredDrinks = if (searchQuery.isEmpty()) allDrinks else {
        allDrinks.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = "home",
                    onNavigateToExplore = onNavigateToExplore,
                    onNavigateToCart = onNavigateToCart,
                    onNavigateToFavorites = onNavigateToFavorites,
                    onNavigateToProfile = onNavigateToProfile,
                    cartCount = cartCount,
                    isDarkMode = isDarkMode
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
            ) {
                item {
                    HeaderWithSearch(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        isDarkMode = isDarkMode
                    )
                }

                item { LoyaltyCard(isDarkMode = isDarkMode) }

                item {
                    CategorySection(
                        categories = CoffeeDatabase.chipscategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        isDarkMode = isDarkMode
                    )
                }


                item {
                    SectionHeader(stringResource(R.string.title_featured),
                stringResource(R.string.see_all), textPrimary, textSecondary,{
                    openProducts()
                        }
                    )}

                items(filteredDrinks.take(3)) { drink ->
                    FeaturedDrinkCardVertical(
                        drink = drink,
                        onClick = { onNavigateToDetails(drink.id) },
                        isDarkMode = isDarkMode
                    )
                }

                item { LimitedTimeOfferBanner(isDarkMode = isDarkMode) }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionHeader(stringResource(R.string.populaire_now), stringResource(R.string.see_all), textPrimary, textSecondary,{
                        openProducts()
                    })
                }

                item {
                    PopularItemsGrid(
                        drinks = filteredDrinks.filter {
                            it.badge!=null && it.badge.contains(badgevalues.Populaire.name)
                        }.take(3),
                        onClick = { onNavigateToDetails(it) },
                        isDarkMode = isDarkMode
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        CoffeeFloatingToggle(onToggle = { DarkModeManager.toggleDarkMode() })
    }
}

@Composable
fun FeaturedDrinkCardVertical(drink: Drink, onClick: () -> Unit, isDarkMode: Boolean) {
    val cardColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else colorsconst.CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(100.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) colorsconst.DarkBorder else colorsconst.BackgroundCream),
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
                }

                if (drink.badge != null) {
                    Surface(
                        color = if (drink.badge.contains("%")) Color(0xFFEF4444) else Color(0xFF10B981),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Text(
                            text = drink.badge,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(drink.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                    Text(drink.description, color = textSecondary, fontSize = 12.sp, maxLines = 1)


                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        if (drink.originalPrice != null) {
                            Text(
                                "${drink.originalPrice}DH",
                                style = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough),
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                        Text("${drink.price}DH", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = textPrimary)
                    }

                    IconButton(
                        onClick = { CoffeeDatabase.addToCart(drink) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isDarkMode) colorsconst.DarkAccent else colorsconst.CoffeeDark,
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LimitedTimeOfferBanner(isDarkMode: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isDarkMode) {
                        listOf(Color(0xFF8D6E63), Color(0xFF5D4037))
                    } else {
                        listOf(Color(0xFFD4A574), Color(0xFF8D6E63))
                    }
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "LIMITED OFFER",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Buy 1 Get 1 Free\non Iced Lattes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 22.sp
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Claim", color = Color(0xFF8D6E63), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HeaderWithSearch(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    val userName = AuthManager.getCurrentUserName()
    val headerPrimary = if (isDarkMode) colorsconst.DarkSurface else colorsconst.CoffeeDark
    val headerSecondary = if (isDarkMode) Color(0xFF5D4037) else Color(0xFF6D4C41)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(headerPrimary, headerSecondary)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning,",
                        color = if (isDarkMode) colorsconst.DarkTextSecondary else colorsconst.CoffeeAccent,
                        fontSize = 14.sp
                    )
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                            .border(2.dp, headerPrimary, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val searchBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
            val searchText = if (isDarkMode) colorsconst.DarkTextPrimary else colorsconst.CoffeeDark

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search coffee...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = searchText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = searchBg,
                    unfocusedContainerColor = searchBg,
                    disabledContainerColor = searchBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = searchText,
                    unfocusedTextColor = searchText
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun LoyaltyCard(isDarkMode: Boolean) {
    val cardBg = if (isDarkMode) Color(0xFF2D2D2D) else Color(0xFF3E2723)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-25).dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Loyalty Card",
                    color = if (isDarkMode) colorsconst.DarkTextSecondary else colorsconst.CoffeeAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "4/8 Stamps",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFFD4A574),
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("☕", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun CategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isDarkMode: Boolean
) {
    val selectedBg = if (isDarkMode) colorsconst.DarkAccent else colorsconst.CoffeeDark
    val unselectedBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val selectedText = if (isDarkMode) colorsconst.CoffeeDark else Color.White
    val unselectedText = if (isDarkMode) colorsconst.DarkTextPrimary else Color(0xFF6D4C41)
    val borderColor = if (isDarkMode) colorsconst.DarkBorder else Color(0xFFE0E0E0)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) selectedBg else unselectedBg)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) selectedBg else borderColor,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (category) {
                        "Hot Coffee" -> "🔥 Hot"
                        "Iced" -> "❄️ Iced"
                        "Bakery" -> "🥐 Food"
                        else -> category
                    },
                    color = if (isSelected) selectedText else unselectedText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PopularItemsGrid(drinks: List<Drink>, onClick: (Int) -> Unit, isDarkMode: Boolean) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        drinks.chunked(2).forEach { rowDrinks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowDrinks.forEach { drink ->
                    PopularItemCardStyled(
                        drink = drink,
                        modifier = Modifier.weight(1f),
                        onClick = { onClick(drink.id) },
                        isDarkMode = isDarkMode
                    )
                }
                if (rowDrinks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PopularItemCardStyled(
    drink: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val cardColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else colorsconst.CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray
    val imageBg = if (isDarkMode) colorsconst.DarkBorder else colorsconst.BackgroundCream

    Card(
        modifier = modifier
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .clickable { CoffeeDatabase.toggleFavorite(drink.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (drink.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (drink.isFavorite) Color(0xFFEF4444) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = colorsconst.RatingYellow, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = drink.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textPrimary
            )
            Text(
                text = drink.description,
                color = textSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${drink.price}DH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary
                )
                IconButton(
                    onClick = { CoffeeDatabase.addToCart(drink) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isDarkMode) colorsconst.DarkAccent else colorsconst.CoffeeDark,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String, textPrimary: Color, textSecondary: Color,onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        if (action.isNotEmpty()) {
            Text(
                text = action,
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onClick()}
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onNavigateToExplore: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    cartCount: Int,
    isDarkMode: Boolean
) {
    val navBg = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val selectedColor = if (isDarkMode) colorsconst.DarkAccent else colorsconst.CoffeeDark
    val unselectedColor = if (isDarkMode) colorsconst.DarkTextSecondary else Color.Gray
    val fabColor = if (isDarkMode) colorsconst.DarkAccent else colorsconst.CoffeeDark

    Box(modifier = Modifier.fillMaxWidth()) {
        NavigationBar(
            containerColor = navBg,
            tonalElevation = 16.dp,
            modifier = Modifier.height(80.dp)
        ) {
            NavBarItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "Home",
                isSelected = selectedTab == "home",
                onClick = {},
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
            NavBarItem(
                icon = Icons.Outlined.Explore,
                selectedIcon = Icons.Filled.Explore,
                label = "Explore",
                isSelected = selectedTab == "explore",
                onClick = onNavigateToExplore,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
            Spacer(modifier = Modifier.weight(1f))
            NavBarItem(
                icon = Icons.Outlined.FavoriteBorder,
                selectedIcon = Icons.Filled.Favorite,
                label = "Saved",
                isSelected = selectedTab == "favorites",
                onClick = onNavigateToFavorites,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
            NavBarItem(
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                label = "Profile",
                isSelected = selectedTab == "profile",
                onClick = onNavigateToProfile,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(fabColor, CircleShape)
                    .clickable { onNavigateToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(Color(0xFFEF4444), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.NavBarItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) selectedColor else unselectedColor
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}