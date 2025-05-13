package com.example.dementia_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class SoundMatchGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundMatchGameScreen()
        }
    }
}

@Composable
fun SoundMatchGameScreen() {
    val soundToAnimal = mapOf(
        "Woof" to "Dog",
        "Meow" to "Cat",
        "Moo" to "Cow",
        "Neigh" to "Horse",
        "Quack" to "Duck",
        "Baa" to "Sheep",
        "Roar" to "Lion",
        "Tweet" to "Bird",
        "Hiss" to "Snake"
    )

    var currentSound by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf<String>()) }
    var correctAnswer by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    val context = LocalContext.current

    // AI-like variation logic
    fun generateNewRound() {
        val entries = soundToAnimal.entries.shuffled()
        val selected = entries.first()
        currentSound = selected.key
        correctAnswer = selected.value

        val wrongOptions = entries.drop(1).map { it.value }.shuffled().take(2)
        options = (wrongOptions + correctAnswer).shuffled()
        showResult = false
    }

    LaunchedEffect(Unit) {
        generateNewRound()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sound Match Game", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        Text("What animal makes this sound?", style = MaterialTheme.typography.titleMedium)
        Text(
            "\"$currentSound\"",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(16.dp)
        )

        options.forEach { option ->
            Button(
                onClick = {
                    showResult = true
                    if (option == correctAnswer) {
                        score++
                        Toast.makeText(context, "Correct! 🎉", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Oops! It's $correctAnswer 🐾", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (showResult) {
            Button(
                onClick = {
                    generateNewRound()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Next Sound")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text("Score: $score", style = MaterialTheme.typography.titleMedium)
    }
}
