package com.example.dementia_app

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dementia_app.Contact  // Ensure this is imported correctly
import com.example.dementia_app.ui.theme.Dementia_AppTheme
import java.util.*

class SeeContactsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dementia_AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PatientViewContactsScreen()
                }
            }
        }
    }
}


@Composable
fun PatientViewContactsScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("contacts", Context.MODE_PRIVATE)
    var contactList by remember { mutableStateOf(listOf<Contact>()) }

    LaunchedEffect(Unit) {
        contactList = loadContacts1(sharedPreferences, context)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Beige background
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Emergency Contacts",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (contactList.isEmpty()) {
            Text("No contacts found.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contactList) { contact ->
                    ContactCardReadOnly(contact)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


@Composable
fun ContactCardReadOnly(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(contact.imageUri),
                contentDescription = "Contact image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${contact.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Phone: ${contact.phone}", style = MaterialTheme.typography.bodyMedium)
            Text("Description: ${contact.description}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun loadContacts1(sharedPreferences: SharedPreferences, context: Context): List<Contact> {
    val contactsJson = sharedPreferences.getString("contact_list", "") ?: return emptyList()

    return contactsJson.split("|").mapNotNull {
        val parts = it.split("#")
        if (parts.size == 5) {
            try {
                Contact(
                    id = UUID.fromString(parts[0]),
                    name = parts[1],
                    phone = parts[2],
                    description = parts[3],
                    imageUri = decodeBase64ToUri1(parts[4], context) ?: Uri.EMPTY
                )
            } catch (e: Exception) {
                null
            }
        } else null
    }
}


fun decodeBase64ToUri1(base64: String, context: Context): Uri? {
    return try {
        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        val file = java.io.File(context.cacheDir, "temp_image_${UUID.randomUUID()}.jpg")
        file.writeBytes(bytes)
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }
}
