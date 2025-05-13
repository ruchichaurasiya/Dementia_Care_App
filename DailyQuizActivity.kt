package com.example.dementia_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import kotlin.random.Random
import java.util.Calendar


class DailyQuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyQuizScreen()
        }
    }
}


@Composable
fun DailyQuizScreen() {
    val context = LocalContext.current

    val morningQuestions = listOf(
        "What did you eat for breakfast today?",
        "Did you brush your teeth this morning?",
        "Who helped you get dressed today?",
        "Did you take your medicine today?"
    )

    val afternoonQuestions = listOf(
        "What color shirt are you wearing now?",
        "Did you talk to someone today? Who?",
        "What did you have for lunch?",
        "What room are you in currently?"
    )

    val eveningQuestions = listOf(
        "What did you watch on TV today?",
        "What was the most enjoyable part of your day?",
        "Did you go outside today?",
        "What will you eat for dinner?"
    )

    val randomTrivia = listOf(
        "Which month is your birthday in?",
        "What is the name of your caregiver?",
        "How old are you?",
        "Do you know what day it is today?"
    )

    // Dynamic time-based selection

    fun getCurrentQuestion(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val questionsPool = when (hour) {
            in 5..11 -> morningQuestions
            in 12..16 -> afternoonQuestions
            else -> eveningQuestions
        }

        val selected = questionsPool.shuffled().take(2) + randomTrivia.shuffled().take(1)
        return selected.random()
    }
    var question by remember { mutableStateOf(getCurrentQuestion()) }
    var answer by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("🧠 Daily Memory Quiz", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            question,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Your Answer") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                if (answer.trim().isNotEmpty()) {
                    Toast.makeText(context, "Answer saved: \"$answer\"", Toast.LENGTH_SHORT).show()
                    answer = ""
                    question = getCurrentQuestion()
                } else {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Submit Answer")
        }
    }
}
