package com.example.f053.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.f053.R
import com.example.f053.colorsconst
import com.example.f053.colorsconst.BackgroundCream
import com.example.f053.colorsconst.CoffeeDark
import com.example.f053.components.CoffeeFloatingToggle
import com.example.f053.db.CoffeeDatabase
import com.example.Cafephile.DarkModeManager
import com.example.f053.models.GalleryPhoto

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalleryScreen(
                photos = getGalleryPhotos(),
                onBack = { finish() },
                onPhotoClick = { /* TODO: open fullscreen viewer */ }
            )
        }
    }

    private fun getGalleryPhotos(): List<GalleryPhoto> {
        return CoffeeDatabase.drinks.mapIndexed { idx, d ->
            GalleryPhoto(
                imageRes = d.imageRes,
                username = "user${idx + 1}",
                likes = 50 + idx * 10
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    photos: List<GalleryPhoto>,
    onBack: () -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit
) {
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
                            "#CoffeeVibes Gallery",
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(photos) { index, photo ->
                    GalleryCard(
                        photo = photo,
                        index = index,
                        onClick = { onPhotoClick(photo) },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        CoffeeFloatingToggle(onToggle = { DarkModeManager.toggleDarkMode() })
    }
}

@Composable
fun GalleryCard(
    photo: GalleryPhoto,
    index: Int,
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val heights = listOf(160.dp, 220.dp, 180.dp, 200.dp)
    val height = heights[index % heights.size]
    val cardBg = if (isDarkMode) colorsconst.DarkSurface else Color.White

    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Image(
            painter = painterResource(id = photo.imageRes),
            contentDescription = photo.username,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}