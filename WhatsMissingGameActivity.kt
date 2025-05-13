package com.example.dementia_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlin.random.Random

class WhatsMissingGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhatsMissingGameScreen()
        }
    }
}


@Composable
fun WhatsMissingGameScreen() {
    val allItems = listOf(
        AIItem("🍎", "Apple"),
        AIItem("📱", "Phone"),
        AIItem("🕶️", "Sunglasses"),
        AIItem("🖼️", "Picture Frame"),
        AIItem("🎩", "Hat")
    )

    var showAll by remember { mutableStateOf(true) }
    var questionItems by remember { mutableStateOf(listOf<AIItem>()) }
    var visibleItems by remember { mutableStateOf(listOf<AIItem>()) }
    var missingItem by remember { mutableStateOf<AIItem?>(null) }
    var selectedAnswer by remember { mutableStateOf<AIItem?>(null) }
    var resultText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Randomly shuffle and choose 5
        questionItems = allItems.shuffled()
        // Show all for 3 seconds
        delay(3000)
        // Hide 1 randomly
        missingItem = questionItems.random()
        visibleItems = questionItems.filter { it != missingItem }
        showAll = false
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "What's Missing?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show emojis (all or with one missing)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayItems = if (showAll) questionItems else visibleItems
            displayItems.forEach {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.emoji, fontSize = 28.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!showAll) {
            Text("Which one is missing?", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))

            // Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                questionItems.forEach { item ->
                    Button(
                        onClick = {
                            selectedAnswer = item
                            resultText = if (item == missingItem) "✅ Correct!" else "❌ Incorrect! Missing was ${missingItem?.label}"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(item.emoji)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (resultText.isNotEmpty()) {
                Text(resultText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class AIItem(
    val emoji: String,
    val label: String
)
