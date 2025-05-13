package com.example.dementia_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class SequenceMemoryGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SequenceGameScreen()
        }
    }
}

@Composable
fun SequenceGameScreen() {
    val symbols = listOf("🔵", "🟢", "🟡", "🔴", "🟣", "🟤")
    var sequence by remember { mutableStateOf(listOf<String>()) }
    var userInput by remember { mutableStateOf(listOf<String>()) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(sequence.size) {
        if (userInput.size == sequence.size && sequence.isNotEmpty()) {
            isCorrect = sequence == userInput
            showResult = true
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Memorize the sequence!", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(20.dp))

        Text(sequence.joinToString(" "), style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(20.dp))

        Row {
            symbols.forEach { symbol ->
                Text(symbol, fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            if (!showResult) {
                                userInput = userInput + symbol
                            }
                        })
            }
        }

        Spacer(Modifier.height(20.dp))

        if (showResult) {
            Text(if (isCorrect) "Correct!" else "Try Again!", color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

            Button(onClick = {
                sequence = sequence + symbols.random()
                userInput = listOf()
                showResult = false
            }) {
                Text("Next Round")
            }
        } else {
            Button(onClick = {
                sequence = listOf(symbols.random())
                userInput = listOf()
            }) {
                Text("Start")
            }
        }
    }
}
