package com.example.dementia_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.security.MessageDigest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.contains("username")

        if (!isRegistered) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        } else {
            setContent {
                LoginScreen()
            }
        }
    }
}


@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.log2_bg), // replace with your actual image name
            contentDescription = "Login Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Caregiver Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val storedUsername = sharedPreferences.getString("username", null)
                    val storedPassword = sharedPreferences.getString("password", null)

                    if (storedUsername == null) {
                        Toast.makeText(
                            context,
                            "No caregiver registered. Please register first.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (username == storedUsername && hashPassword1(password) == storedPassword) {
                        saveLoginState(context, true)
                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, CaregiverActivity::class.java))
                    } else {
                        loginError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            if (loginError) {
                Text("Invalid username or password", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            ForgetPasswordActivity::class.java
                        )
                    )
                }
            ) {
                Text("Forgot Password?")
            }

            TextButton(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            UpdatePasswordActivity::class.java
                        )
                    )
                }
            ) {
                Text("Update Password")
            }

            // ✅ Add "Register" button for users to manually navigate to registration
            TextButton(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }
            ) {
                Text("Not registered? Create an account")
            }

        }
    }

}




// ✅ Hash password function (SHA-256)
fun hashPassword1(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}

// ✅ Save login state
fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
}
