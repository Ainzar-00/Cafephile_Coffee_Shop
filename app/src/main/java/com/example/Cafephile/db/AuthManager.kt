package com.example.f053.db

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import com.example.f053.models.User
import org.json.JSONArray
import org.json.JSONObject

data class AuthUser(
    val username: String,
    val name: String,
    val password: String
)

object AuthManager {
    private const val PREFS_NAME = "CoffeeShopPrefs"
    private const val KEY_ALL_USERS = "allUsers"
    private const val KEY_CURRENT_USERNAME = "currentUsername"

    private lateinit var prefs: SharedPreferences

    // In-memory storage for registered users
    private val registeredUsers = mutableListOf<AuthUser>()

    var currentUser = mutableStateOf<AuthUser?>(null)
        private set

    var isLoggedIn = mutableStateOf(false)
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadAllUsers()
        loadCurrentSession()
    }

    private fun loadAllUsers() {
        val usersJson = prefs.getString(KEY_ALL_USERS, null)
        if (usersJson != null) {
            try {
                val jsonArray = JSONArray(usersJson)
                registeredUsers.clear()

                for (i in 0 until jsonArray.length()) {
                    val userObj = jsonArray.getJSONObject(i)
                    val user = AuthUser(
                        username = userObj.getString("username"),
                        name = userObj.getString("name"),
                        password = userObj.getString("password")
                    )
                    registeredUsers.add(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveAllUsers() {
        try {
            val jsonArray = JSONArray()
            registeredUsers.forEach { user ->
                val userObj = JSONObject().apply {
                    put("username", user.username)
                    put("name", user.name)
                    put("password", user.password)
                }
                jsonArray.put(userObj)
            }

            prefs.edit()
                .putString(KEY_ALL_USERS, jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCurrentSession() {
        val currentUsername = prefs.getString(KEY_CURRENT_USERNAME, null)
        if (currentUsername != null) {
            val user = registeredUsers.find { it.username == currentUsername }
            if (user != null) {
                currentUser.value = user
                isLoggedIn.value = true
            } else {
                // User data was corrupted or deleted, clear session
                clearCurrentSession()
            }
        }
    }

    private fun saveCurrentSession(username: String) {
        prefs.edit()
            .putString(KEY_CURRENT_USERNAME, username)
            .apply()
    }

    private fun clearCurrentSession() {
        prefs.edit()
            .remove(KEY_CURRENT_USERNAME)
            .apply()
    }

    fun register(username: String, password: String, name: String): Result<String> {
        return try {
            // Validation
            if (username.isBlank()) {
                return Result.failure(Exception("Username is required"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("Password is required"))
            }
            if (name.isBlank()) {
                return Result.failure(Exception("Name is required"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            // Check if user already exists
            if (registeredUsers.any { it.username == username }) {
                return Result.failure(Exception("Username already exists"))
            }

            // Register user
            val newUser = AuthUser(username, name, password)
            registeredUsers.add(newUser)

            // Save all users to SharedPreferences
            saveAllUsers()

            // Auto login after registration
            loginUser(newUser)

            Result.success("Registration successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun login(username: String, password: String): Result<String> {
        return try {
            // Validation
            if (username.isBlank()) {
                return Result.failure(Exception("Username is required"))
            }
            if (password.isBlank()) {
                return Result.failure(Exception("Password is required"))
            }

            // Find user
            val user = registeredUsers.find {
                it.username == username && it.password == password
            }

            if (user == null) {
                return Result.failure(Exception("Invalid username or password"))
            }

            // Login user
            loginUser(user)

            Result.success("Login successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun loginUser(user: AuthUser) {
        currentUser.value = user
        isLoggedIn.value = true

        // Save current session
        saveCurrentSession(user.username)
    }

    fun logout() {
        currentUser.value = null
        isLoggedIn.value = false

        // Clear current session only (keep all users)
        clearCurrentSession()
    }

    fun getCurrentUserName(): String {
        return currentUser.value?.name ?: "Guest"
    }

    fun getCurrentUsername(): String? {
        return currentUser.value?.username
    }

    // For debugging/testing - get all registered usernames
    fun getAllUsernames(): List<String> {
        return registeredUsers.map { it.username }
    }
}