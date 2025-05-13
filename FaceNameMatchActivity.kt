package com.example.dementia_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlin.random.Random

class FaceNameMatchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FaceNameMatchGame()
            }
        }
    }
}


@Composable
fun FaceNameMatchGame() {
    // Placeholder AI face API and name pool
    val names = listOf("Sophia", "Jackson", "Mia", "Aiden", "Olivia", "Ethan", "Ava", "Lucas", "Emma", "Noah")
    val imageUrl = "https://thispersondoesnotexist.com/"

    var score by remember { mutableStateOf(0) }
    var round by remember { mutableStateOf(1) }
    var correctName by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf<String>()) }
    var selectedName by remember { mutableStateOf<String?>(null) }
    var faceUrl by remember { mutableStateOf("") }

    fun generateNewRound() {
        faceUrl = "$imageUrl?rand=${System.currentTimeMillis()}"
        correctName = names.random()
        val distractors = names.filterNot { it == correctName }.shuffled().take(2)
        options = (distractors + correctName).shuffled()
        selectedName = null
    }

    // Load new round on start or when moving to next
    LaunchedEffect(round) {
        generateNewRound()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Match the Name to the Face", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Image(
            painter = rememberAsyncImagePainter(faceUrl),
            contentDescription = "AI Face",
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        )

        Spacer(Modifier.height(16.dp))
        options.forEach { name ->
            Button(
                onClick = {
                    selectedName = name
                    if (name == correctName) score++
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedName == name) {
                        if (name == correctName) Color(0xFFB2FF59) else Color(0xFFFF8A80)
                    } else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(name, fontSize = 18.sp)
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Score: $score / $round", fontSize = 18.sp)

        if (selectedName != null) {
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                round++
            }) {
                Text("Next")
            }
        }
    }
}