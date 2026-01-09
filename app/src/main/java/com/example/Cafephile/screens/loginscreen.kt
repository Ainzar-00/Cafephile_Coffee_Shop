package com.example.f053.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f053.R
import com.example.f053.db.AuthManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val loginFailedText = stringResource(R.string.error_login_failed)

    var isRegistrationMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = if (isRegistrationMode) "Create Account" else "Welcome Back",
                fontSize = 34.sp,
                color = Color(0xFFF5F5F5),
                fontWeight = FontWeight.SemiBold
            )

            if (!isRegistrationMode) {
                Spacer(modifier = Modifier.height(13.dp))
                Text(
                    text = stringResource(R.string.login_subtitle),
                    fontSize = 18.sp,
                    color = Color(0xFFB0B0B0)
                )
                Spacer(modifier = Modifier.height(40.dp))
            } else {
                Spacer(modifier = Modifier.height(53.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                CoffeeTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        error = ""
                    },
                    label = stringResource(R.string.label_username),
                    placeholder = stringResource(R.string.hint_username)
                )

                PasswordField(
                    password = password,
                    onPasswordChange = {
                        password = it
                        error = ""
                    }
                )

                if (isRegistrationMode) {
                    CoffeeTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            error = ""
                        },
                        label = stringResource(R.string.label_full_name),
                        placeholder = stringResource(R.string.hint_full_name)
                    )
                }
            }

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            Button(
                onClick = {
                    if (isRegistrationMode) {
                        val result = AuthManager.register(username, password, name)
                        result.onSuccess {
                            onLoginSuccess()
                        }.onFailure {
                            error = it.message ?: loginFailedText
                        }
                    } else {
                        val result = AuthManager.login(username, password)
                        result.onSuccess {
                            onLoginSuccess()
                        }.onFailure {
                            error = it.message ?: loginFailedText
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFB923C)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_continue),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = if (isRegistrationMode)
                            stringResource(R.string.login_have_account)
                        else
                            stringResource(R.string.login_no_account),
                        fontSize = 14.sp,
                        color = Color(0xFFB0B0B0)
                    )

                    Text(
                        text = if (isRegistrationMode) "Sign In" else "Sign Up",
                        fontSize = 14.sp,
                        color = Color(0xFFFB923C),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            isRegistrationMode = !isRegistrationMode
                            error = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CoffeeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFE5E5E5),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF707070)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFB923C),
                unfocusedBorderColor = Color(0xFF3A3A3A),
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFFB923C)
            ),
            singleLine = true
        )
    }
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.label_password),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFE5E5E5),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text(stringResource(R.string.hint_password), color = Color(0xFF707070)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFB923C),
                unfocusedBorderColor = Color(0xFF3A3A3A),
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFFB923C)
            ),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0xFF999999)
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}