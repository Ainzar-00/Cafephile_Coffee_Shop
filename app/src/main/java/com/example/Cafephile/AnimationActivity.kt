package com.example.f053.screens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.f053.AnimationManager
import com.example.f053.MainActivity
import com.example.f053.R
import com.example.myapplication.SplashScreenActivity1
import kotlinx.coroutines.delay

class AnimationActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoffeeAnimation(
                onComplete = {
                    AnimationManager.markAnimationShown()

                    val intent = Intent(this@AnimationActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SplashScreenActivity1.preloadedPlayer?.release()
        SplashScreenActivity1.preloadedPlayer = null
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun CleanVideoPlayer(exoPlayer: ExoPlayer?) {
    if (exoPlayer == null) return

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                controllerAutoShow = false
                controllerHideOnTouch = true
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        }
    )
}

@Composable
fun TypewriterText(
    fullText: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    fontSize: Int = 40,
    textColor: Color = Color.White,
    charDelay: Long = 50L
) {
    var displayedText by remember { mutableStateOf("") }
    var maxWidth by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                maxWidth = coordinates.size.width
            }
    ) {
        val textMeasurer = rememberTextMeasurer()

        LaunchedEffect(fullText, maxWidth) {
            if (maxWidth == 0) return@LaunchedEffect

            displayedText = ""

            val singleCharWidth = textMeasurer.measure(
                text = "A",
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = fontSize.sp
                )
            ).size.width

            val words = fullText.split(" ")
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"

                val lineWidth = textMeasurer.measure(
                    text = testLine,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = fontSize.sp
                    )
                ).size.width

                if (lineWidth > maxWidth && currentLine.isNotEmpty()) {
                    val startLength = displayedText.length
                    for (i in currentLine.indices) {
                        displayedText = displayedText.substring(0, startLength) + currentLine.substring(0, i + 1)
                        delay(charDelay)
                    }
                    displayedText += "\n"
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }

            val startLength = displayedText.length
            for (i in currentLine.indices) {
                displayedText = displayedText.substring(0, startLength) + currentLine.substring(0, i + 1)
                delay(charDelay)
            }
        }

        Text(
            text = displayedText,
            fontFamily = fontFamily,
            fontSize = fontSize.sp,
            color = textColor
        )
    }
}

@Composable
fun PlayAudio(resourceId: Int) {
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, resourceId)?.apply {
            setVolume(1.0f, 1.0f)
        }
    }

    LaunchedEffect(Unit) {
        try {
            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        }
    }
}

@Composable
fun CoffeeAnimation(onComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(10000L)
        onComplete()
    }

    LaunchedEffect(Unit) {
        SplashScreenActivity1.preloadedPlayer?.playWhenReady = true
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val paddingHorizontal = maxWidth * 0.1f
        val paddingVertical = maxHeight * 0.05f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onComplete()
                }
        ) {
            CleanVideoPlayer(exoPlayer = SplashScreenActivity1.preloadedPlayer)

            PlayAudio(R.raw.sound1)

            val myFont = FontFamily(Font(R.font.font2))
            TypewriterText(
                fullText = """
                    Behind every successful person is a substantial amount of coffee.
                     — Stephanie Piro
                """.trimIndent(),
                modifier = Modifier
                    .padding(start = paddingHorizontal, top = paddingVertical),
                fontFamily = myFont,
                fontSize = 50,
                textColor = Color.Black,
                charDelay = 80L
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Tap anywhere to skip",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}