package com.example.f053.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f053.MainActivity
import com.example.f053.db.CoffeeDatabase
import com.example.Cafephile.DarkModeManager
import com.example.f053.models.CartItem
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.f053.R
import com.example.f053.colorsconst
import com.example.f053.colorsconst.BackgroundCream
import com.example.f053.colorsconst.CoffeeDark
import com.example.f053.colorsconst.CoffeeMedium
import com.example.f053.colorsconst.Gold
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.models.SizeEnum

private val AlertRed = Color(0xFFEF4444)

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "cart"
            ) {
                composable("cart") {
                    CartScreenContent(
                        onBack = { finish() },
                        onNavigateToHome = {
                            val intent = Intent(this@CartActivity, MainActivity::class.java).apply {
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
                        onNavigateToCart = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProductDetails: (Int) -> Unit
) {
    val cartItems = CoffeeDatabase.cartItems
    val cartTotal = CoffeeDatabase.getCartTotal()
    val context = LocalContext.current

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
                            stringResource(R.string.title_my_order),
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                stringResource(R.string.nav_back),
                                tint = textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            },
            bottomBar = {
                if (cartItems.isNotEmpty()) {
                    CartBottomBar(
                        total = cartTotal,
                        onCheckout = {
                            Toast.makeText(
                                context,
                                "Order placed successfully! Your coffee is on its way ☕",
                                Toast.LENGTH_LONG
                            ).show()

                             Handler(Looper.getMainLooper()).postDelayed({
                                 CoffeeDatabase.clearCart()
                                 onNavigateToHome()
                             }, 1500)
                        },
                        isDarkMode = isDarkMode
                    )
                }
            }
        ) { padding ->
            if (cartItems.isEmpty()) {
                EmptyCartView(
                    onStartShopping = onNavigateToHome,
                    modifier = Modifier.padding(padding),
                    isDarkMode = isDarkMode
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.label_review_items),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                    }

                    items(
                        items = cartItems,
                        key = { "${it.drink.id}-${it.selectedSize}" }
                    ) { cartItem ->
                        SwipeToDeleteCartItem(
                            cartItem = cartItem,
                            onDelete = {
                                CoffeeDatabase.removeFromCart(cartItem)
                            },
                            isDarkMode = isDarkMode,
                            onClick = {
                                onNavigateToProductDetails(cartItem.drink.id)
                            }
                        )
                    }
                }
            }
        }

        CoffeeFloatingToggle(
            onToggle = { DarkModeManager.toggleDarkMode() }
        )
    }
}

@Composable
fun SwipeToDeleteCartItem(
    cartItem: CartItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isDarkMode: Boolean = false
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 150f
    var isRemoved by remember { mutableStateOf(false) }

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(500)
            onDelete()
        }
    }

    val offsetXAnimated by animateFloatAsState(
        targetValue = if (isRemoved) -1000f else offsetX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "drag"
    )

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            if (offsetXAnimated < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.padding(end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.action_remove),
                            color = AlertRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            tint = AlertRed
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetXAnimated.roundToInt(), 0) }
                    .clickable { onClick() }
                    .pointerInput(cartItem.drink.id) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX < -dismissThreshold) {
                                    isRemoved = true
                                } else {
                                    offsetX = 0f
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val newOffset = offsetX + dragAmount
                                if (!isRemoved) {
                                    offsetX = newOffset.coerceIn(-1000f, 0f)
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                CartItemContent(cartItem, isDarkMode)
            }
        }
    }
}

@Composable
fun CartItemContent(cartItem: CartItem, isDarkMode: Boolean = false) {
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val imageBg = if (isDarkMode) colorsconst.DarkSurface else BackgroundCream

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(90.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(imageBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = cartItem.drink.imageRes),
                contentDescription = cartItem.drink.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = cartItem.drink.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${cartItem.selectedSize} | ${
                        when (cartItem.selectedSize) {
                            SizeEnum.Small.name -> "8 oz"
                            SizeEnum.Medium.name -> "12 oz"
                            SizeEnum.Large.name -> "16 oz"
                            else -> ""
                        }
                    }",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (cartItem.drink.originalPrice != null) {
                        Text(
                            text = "${String.format("%.2f", cartItem.drink.originalPrice * cartItem.sizeMultiplier)}DH",
                            fontSize = 12.sp,
                            color = if (isDarkMode) colorsconst.DarkTextSecondary.copy(alpha = 0.6f) else Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Text(
                        text = "${String.format("%.2f", cartItem.subtotal)}DH",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                }

                QuantitySelectorPill(
                    qty = cartItem.quantity,
                    onIncrease = {
                        CoffeeDatabase.updateCartItemQuantity(cartItem, cartItem.quantity + 1)
                    },
                    onDecrease = {
                        CoffeeDatabase.updateCartItemQuantity(cartItem, cartItem.quantity - 1)
                    },
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
fun QuantitySelectorPill(
    qty: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    isDarkMode: Boolean = false
) {
    val textColor = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val borderColor = if (isDarkMode) colorsconst.DarkBorder else Color(0xFFEEEEEE)
    val bgColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(36.dp)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .background(bgColor, RoundedCornerShape(50))
            .padding(horizontal = 4.dp)
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(28.dp),
            enabled = qty > 1
        ) {
            Icon(
                Icons.Rounded.Remove,
                contentDescription = stringResource(R.string.action_remove),
                tint = if(qty > 1) textColor else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = "$qty",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(28.dp)
        ) {
            Box(
                modifier = Modifier.size(22.dp).background(buttonBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun CartBottomBar(
    total: Double,
    onCheckout: () -> Unit,
    isDarkMode: Boolean = false
) {
    val bgColor = if (isDarkMode) colorsconst.DarkSurface else Color.White
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val dividerColor = if (isDarkMode) colorsconst.DarkDivider else Color(0xFFF3F4F6)

    Surface(
        color = bgColor,
        modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Subtotal", color = textSecondary, fontSize = 14.sp)
                Text("${String.format("%.2f", total)}DH", color = textPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Delivery Fee", color = textSecondary, fontSize = 14.sp)
                Text("2.50DH", color = textPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.label_total), color = textSecondary, fontSize = 12.sp)
                    Text(
                        "${String.format("%.2f", total + 2.50)}DH",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                }

                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .height(54.dp)
                        .width(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyCartView(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val bgCircle = if (isDarkMode) colorsconst.DarkSurface else BackgroundCream
    val textPrimary = if (isDarkMode) colorsconst.DarkTextPrimary else CoffeeDark
    val textSecondary = if (isDarkMode) colorsconst.DarkTextSecondary else CoffeeMedium
    val buttonBg = if (isDarkMode) colorsconst.DarkAccent else CoffeeDark

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(bgCircle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Gold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            stringResource(R.string.empty_cart_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Text(
            stringResource(R.string.empty_cart_message),
            fontSize = 14.sp,
            color = textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartShopping,
            colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(50.dp)
        ) {
            Text(stringResource(R.string.action_browse_menu))
        }
    }
}