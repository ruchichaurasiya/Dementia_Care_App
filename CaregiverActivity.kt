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
import androidx.compose.runtime.Composable
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

class CaregiverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkAuthentication(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            Dementia_AppTheme {
                CaregiverDashboardScreen()
            }
        }
    }
}

@Composable
fun CaregiverDashboardScreen() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔼 App logo - top-right layer
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

        // 🔽 Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)) // Beige background
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Caregiver Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF512DA8)
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardCard(
                    iconId = R.drawable.medication_icon_rbg,
                    label = "Medications",
                    backgroundColor = Color(0xFFC8E6C9)
                ) {
                    context.startActivity(Intent(context, MedicationActivity::class.java))
                }

                DashboardCard(
                    iconId = R.drawable.contact_icon,
                    label = "Contacts",
                    backgroundColor = Color(0xFFFFF9C4)
                ) {
                    context.startActivity(Intent(context, ContactActivity::class.java))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardCard(
                    iconId = R.drawable.buzzer_icon,
                    label = "Buzzer Alarm",
                    backgroundColor = Color(0xFFFFCDD2)
                ) {
                    context.startActivity(Intent(context, AlarmActivity::class.java))
                }

                DashboardCard(
                    iconId = R.drawable.voice_icon_rbg,
                    label = "Voice Assist",
                    backgroundColor = Color(0xFFBBDEFB)
                ) {
                    context.startActivity(Intent(context, VoiceAssistantActivity::class.java))
                }
            }

            Spacer(modifier = Modifier.height(55.dp))

            Button(
                onClick = { logoutUser(context) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text("Logout", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DashboardCard(iconId: Int, label: String, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = label,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 16.sp, color = Color.Black)
        }
    }
}

fun checkAuthentication(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}

fun logoutUser(context: Context) {
    val sharedPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
