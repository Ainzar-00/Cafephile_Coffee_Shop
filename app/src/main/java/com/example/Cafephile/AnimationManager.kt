package com.example.f053

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*


object AnimationManager {
    private const val PREFS_NAME = "animation_prefs"
    private const val KEY_LAST_SHOWN_DATE = "last_shown_date"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun shouldShowAnimation(): Boolean {
        if (!::sharedPreferences.isInitialized) {
            return false
        }

        val lastShownDate = sharedPreferences.getString(KEY_LAST_SHOWN_DATE, null)
        val today = getCurrentDate()

        return lastShownDate != today
    }


    fun markAnimationShown() {
        if (!::sharedPreferences.isInitialized) return

        sharedPreferences.edit()
            .putString(KEY_LAST_SHOWN_DATE, getCurrentDate())
            .apply()
    }


    fun resetAnimationState() {
        if (!::sharedPreferences.isInitialized) return

        sharedPreferences.edit()
            .remove(KEY_LAST_SHOWN_DATE)
            .apply()
    }


    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}