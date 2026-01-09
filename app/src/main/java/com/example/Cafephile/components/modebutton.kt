package com.example.f053.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.f053.R

@Composable
fun CoffeeFloatingToggle(
    onToggle: (Boolean) -> Unit
) {
    var isToggled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(bottom = 140.dp)
        ,
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier
                .size(60.dp) // Size of the white square container
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clickable {
                    isToggled = !isToggled
                    onToggle(isToggled)
                },
            shape = RoundedCornerShape(16.dp),
            color = if (isToggled) Color(0xFFFDF7F2) else Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bean),
                    contentDescription = "Toggle Coffee Mode",
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer(
                            scaleX = if (isToggled) 1.1f else 1f,
                            scaleY = if (isToggled) 1.1f else 1f
                        )
                )
            }
        }
    }
}