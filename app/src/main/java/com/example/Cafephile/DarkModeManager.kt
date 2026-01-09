package com.example.Cafephile


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object DarkModeManager {
    private const val PREFS_NAME = "dark_mode_prefs"
    private const val KEY_DARK_MODE = "is_dark_mode"

    private lateinit var sharedPreferences: SharedPreferences

    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, _isDarkMode.value)
            .apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }
}