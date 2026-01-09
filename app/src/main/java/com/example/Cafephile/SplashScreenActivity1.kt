package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.f053.screens.AnimationActivity
import com.example.f053.R
import com.example.f053.screens.Splashscreen1
import kotlinx.coroutines.delay


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity1 : ComponentActivity() {

    companion object {
        var preloadedPlayer: ExoPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preloadedPlayer = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${packageName}/${R.raw.video1}")
            setMediaItem(mediaItem)
            volume = 0f
            repeatMode = Player.REPEAT_MODE_OFF
            prepare()
        }

        setContent {
            Splashscreen1(modifier = Modifier.fillMaxSize())

            LaunchedEffect(Unit) {
                delay(2000L)
                val intent = Intent(this@SplashScreenActivity1, AnimationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}

