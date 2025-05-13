package com.example.dementia_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dementia_app.ui.theme.Dementia_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dementia_AppTheme {
                ModuleSelectionScreen()
            }
        }
    }
}

@Composable
fun ModuleSelectionScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5DC)) // Beige background
    )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
            )

            Text(
                text = "Dementia Care App",
                fontSize = 18.sp,
                color = Color(0xFF722F37),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "We won't forget, We care",
                fontSize = 13.sp,
                color = Color(0xFF722F37),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Patient Module Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFB2DFDB)) // Light teal green
                    .clickable {
                        context.startActivity(Intent(context, PatientActivity::class.java))
                    }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.patient_icon_rbg),
                        contentDescription = "Patient Icon",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Patient Module", fontSize = 18.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Caregiver Module Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFBBDEFB)) // Light blue
                    .clickable {
                        if (checkAuthenticationMain(context)) {
                            context.startActivity(Intent(context, CaregiverActivity::class.java))
                        } else {
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.caregiver_icon),
                        contentDescription = "Caregiver Icon",
                        modifier = Modifier.size(110.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Caregiver Module", fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    }


fun checkAuthenticationMain(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}
