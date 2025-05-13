package com.example.dementia_app

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.*
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import java.io.File
import com.example.dementia_app.ui.theme.Dementia_AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactScreen()
        }
    }
}

data class Contact(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val phone: String,
    val description: String,
    val imageUri: Uri
)

@Composable
fun ContactScreen() {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactList by remember { mutableStateOf(listOf<Contact>()) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("add") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }  // Track the contact being edited
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("contacts", Context.MODE_PRIVATE)
    val authPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Beige background
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) {
        contactList = loadContacts(sharedPreferences, context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text("Add New Contact", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))


                // Show 'Update Contact' button only if we are editing a contact
                Button(
                    onClick = {
                        if (editingContact != null) {
                            actionType = "edit"  // Set action type to 'edit'
                            showPasswordDialog = true  // Show password dialog to confirm
                        } else {
                            actionType = "add"  // Set action type to 'add' if creating new contact
                            showPasswordDialog = true  // Show password dialog for adding contact
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editingContact == null) "Add Contact" else "Update Contact")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Saved Contacts:", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                contactList.forEach { contact ->
                    ContactCard(contact, onEdit = {
                        name = contact.name
                        phone = contact.phone
                        description = contact.description
                        selectedImageUri = contact.imageUri
                        editingContact = contact  // Mark as editing the contact
                    }, onDelete = {
                        editingContact = contact
                        actionType = "delete"
                        showPasswordDialog = true
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
            CustomScrollbar(scrollState, coroutineScope)
        }
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordEntered = { password ->
                val storedPassword = authPreferences.getString("password", null)
                if (password == storedPassword) {
                    when (actionType) {
                        "add" -> {
                            val newContact = Contact(
                                name = name,
                                phone = phone,
                                description = description,
                                imageUri = selectedImageUri ?: Uri.EMPTY
                            )
                            contactList = contactList + newContact
                            saveContacts(sharedPreferences, contactList, context)
                            selectedImageUri = null // Reset after adding
                            name = "" // Clear the name field after adding
                            phone = "" // Clear the phone field
                            description = "" // Clear the description field
                        }

                        "edit" -> {
                            contactList = contactList.map {
                                if (it.id == editingContact?.id) {
                                    it.copy(name = name, phone = phone, description = description)
                                } else it
                            }
                            saveContacts(sharedPreferences, contactList, context)
                            name = "" // Clear after edit
                            phone = "" // Clear after edit
                            description = "" // Clear after edit
                            editingContact = null // Reset editing contact
                            actionType = "add" // Reset actionType to "add"
                        }



                        "delete" -> {
                            // If we are deleting, we filter out the contact by ID
                            contactList = contactList.filter { it.id != editingContact!!.id }

                            // Ensure we are not deleting all contacts (double-check!)
                            if (contactList.isNotEmpty()) {
                                saveContacts(sharedPreferences, contactList, context) // Save the updated contact list after deletion
                            }
                            editingContact = null // Reset editing contact
                        }
                    }

                    showPasswordDialog = false
                }else {
                    Toast.makeText(
                        context,
                        "Incorrect Password! Unauthorized Access!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}



@Composable
fun CustomScrollbar(scrollState: ScrollState, coroutineScope: CoroutineScope) {
    val coroutineScope = rememberCoroutineScope()
    val proportion = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(8.dp)
            .background(Color.LightGray.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        scrollState.scrollTo((scrollState.value + dragAmount.toInt()).coerceIn(0, scrollState.maxValue))
                    }
                }
            }
    ) {
        drawRoundRect(
            color = Color.DarkGray,
            topLeft = Offset(0f, proportion * size.height),
            size = size.copy(height = 40.dp.toPx())
        )
    }
}

@Composable
fun ContactCard(contact: Contact, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(contact.imageUri),
                contentDescription = "Contact image",
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${contact.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Phone: ${contact.phone}", style = MaterialTheme.typography.bodyMedium)
            Text("Description: ${contact.description}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onEdit) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}



fun saveContacts(sharedPreferences: SharedPreferences, contacts: List<Contact>, context: Context) {
    val editor = sharedPreferences.edit()
    val contactsJson = contacts.joinToString("|") {
        "${it.id}#${it.name}#${it.phone}#${it.description}#${encodeUriToBase64(it.imageUri, context)}"
    }
    editor.putString("contact_list", contactsJson)
    editor.apply()
}


fun loadContacts(sharedPreferences: SharedPreferences, context: Context): List<Contact> {
    val contactsJson = sharedPreferences.getString("contact_list", "") ?: return emptyList()
    return contactsJson.split("|").mapNotNull {
        val parts = it.split("#")
        if (parts.size == 5) { // Adjusting the condition to match correct split length
            Contact(
                id = UUID.fromString(parts[0]),  // Adding the missing ID parsing
                name = parts[1],
                phone = parts[2],
                description = parts[3],
                imageUri = decodeBase64ToUri(parts[4], context) ?: Uri.EMPTY
            )
        } else null
    }
}


fun encodeUriToBase64(uri: Uri, context: Context): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } else {
                TODO("VERSION.SDK_INT < FROYO")
            }
        } else {
            ""
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun decodeBase64ToUri(base64: String, context: Context): Uri? {
    return try {
        val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Base64.decode(base64, Base64.DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < FROYO")
        }
        val file = File(context.cacheDir, "contact_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { it.write(decodedBytes) }
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Preview
@Composable
fun PreviewContactScreen() {
    ContactScreen()
}