package com.example.dementia_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dementia_app.ui.theme.Dementia_AppTheme

class ImagePickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dementia_AppTheme {
                ImagePickerScreen()
            }
        }
    }
}

@Composable
fun ImagePickerScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Image Picker Feature Coming Soon!", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview
@Composable
fun PreviewImagePickerScreen() {
    Dementia_AppTheme {
        ImagePickerScreen()
    }
}
