package com.example.dementia_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dementia_app.ui.theme.Dementia_AppTheme

    class VoiceRecognitionActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                Dementia_AppTheme {
                    VoiceRecognitionScreen()
                }
            }
        }
    }

    @Composable
    fun VoiceRecognitionScreen() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Voice Recognition Feature Coming Soon!", style = MaterialTheme.typography.headlineMedium)
        }
    }

    @Preview
    @Composable
    fun PreviewVoiceRecognitionScreen() {
        Dementia_AppTheme {
            VoiceRecognitionScreen()
        }
    }


