package com.example.dementia_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dementia_app.ui.theme.Dementia_AppTheme
import androidx.compose.ui.draw.clip

class MemoryGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dementia_AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MemoryGameMenu()
                }
            }
        }
    }
}

@Composable
fun MemoryGameMenu() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6)) // Light purple background
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🧠 Memory Boosting Games",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF512DA8)
        )

        Spacer(modifier = Modifier.height(30.dp))

        GameCard(
            iconId = R.drawable.sound_icon,
            label = "Sound Match",
            backgroundColor = Color(0xFFBBDEFB)
        ) {
            context.startActivity(Intent(context, SoundMatchGameActivity::class.java))
        }

        GameCard(
            iconId = R.drawable.quiz,
            label = "Daily Memory Quiz",
            backgroundColor = Color(0xFFFFF9C4)
        ) {
            context.startActivity(Intent(context, DailyQuizActivity::class.java))
        }
    }
}

@Composable
fun GameCard(iconId: Int, label: String, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(220.dp)
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
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
