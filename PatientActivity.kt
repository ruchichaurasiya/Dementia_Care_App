package com.example.dementia_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

class PatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dementia_AppTheme {
                PatientDashboardScreen()
            }
        }
    }
}

@Composable
fun PatientDashboardScreen() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Top-right app logo
        Image(
            painter = painterResource(id = R.drawable.app_logo), // Replace with your actual logo resource
            contentDescription = "App Logo",
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopEnd)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDE7F6)) // Light purple background
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Title
            Text(
                text = "Patient Dashboard",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF512DA8) // Royal purple
            )

            Spacer(modifier = Modifier.height(32.dp))

            DashboardCard1(
                iconId = R.drawable.contact_icon,
                label = "See Contacts",
                backgroundColor = Color(0xFFFFF9C4) // Light Yellow
            ) {
                context.startActivity(Intent(context, SeeContactsActivity::class.java))
            }

            DashboardCard1(
                iconId = R.drawable.game1_rbg,
                label = "Play Games",
                backgroundColor = Color(0xFFC8E6C9) // Light Green
            ) {
                context.startActivity(Intent(context, MemoryGameActivity::class.java))
            }
        }
    }

}
@Composable
fun DashboardCard1(iconId: Int, label: String, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(220.dp) // Compact width
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = label,
                modifier = Modifier.size(90.dp), // Increased icon size
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
