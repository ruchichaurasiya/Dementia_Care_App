package com.example.dementia_app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput




class MedicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicationScreen()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }
}

data class Medication(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val time: String,
    val day: String
)

@Composable
fun MedicationScreen() {
    var medicationName by remember { mutableStateOf("") }
    var medicationTime by remember { mutableStateOf("Select Time") }
    var medicationDay by remember { mutableStateOf("Everyday") }
    var medicationList by remember { mutableStateOf(listOf<Medication>()) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var editingMedication by remember { mutableStateOf<Medication?>(null) }
    var actionType by remember { mutableStateOf("add") }
    val scrollState1 = rememberScrollState()
    val coroutineScope1 = rememberCoroutineScope()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("Medications", Context.MODE_PRIVATE)
    val authPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Beige background
    )

    LaunchedEffect(Unit) {
        medicationList = loadMedications(sharedPreferences)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState1)
                    .padding(16.dp)
            ) {
                Text("Medication Reminder", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = { Text("Enter Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(medicationTime)
                }
                Spacer(modifier = Modifier.height(8.dp))

                val daysOfWeek = listOf("Everyday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(medicationDay)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    medicationDay = day
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (medicationName.isNotBlank() && medicationTime != "Select Time") {
                            showPasswordDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editingMedication == null) "Add Medication" else "Update Medication")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Scheduled Medications:", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                medicationList.forEach { medication ->
                    MedicationCard(medication, onEdit = {
                        editingMedication = medication
                        medicationName = medication.name
                        medicationTime = medication.time
                        medicationDay = medication.day
                        actionType = "edit"
                    }, onDelete = {
                        editingMedication = medication
                        actionType = "delete"
                        showPasswordDialog = true
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            CustomScrollbar1(scrollState1, coroutineScope1)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { selectedTime ->
                medicationTime = selectedTime
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordEntered = { password ->
                val storedPassword = authPreferences.getString("password", null)
                if (password == storedPassword) {
                    when (actionType) {
                        "add" -> {
                            val newMedication = Medication(UUID.randomUUID(), medicationName, medicationTime, medicationDay)
                            medicationList = medicationList + newMedication
                            medicationName = "" // Clear the medication name field after adding
                        }
                        "edit" -> {
                            medicationList = medicationList.map {
                                if (it.id == editingMedication?.id) {
                                    it.copy(name = medicationName, time = medicationTime, day = medicationDay)
                                } else it
                            }
                            editingMedication = null
                            medicationName = ""  // Clear after edit
                            medicationTime = "Select Time"
                            medicationDay = "Everyday"
                            actionType = "add"
                        }
                        "delete" -> {
                            medicationList = medicationList.filter { it.id != editingMedication!!.id }
                        }
                    }
                    saveMedications(sharedPreferences, medicationList)
                    showPasswordDialog = false
                } else {
                    Toast.makeText(context, "Incorrect Password! Unauthorized Access!", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }
}


@Composable
fun CustomScrollbar1(scrollState1: ScrollState, coroutineScope1: CoroutineScope) {
    val coroutineScope = rememberCoroutineScope()
    val proportion = scrollState1.value.toFloat() / scrollState1.maxValue.toFloat()
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(8.dp)
            .background(Color.LightGray.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope1.launch {
                        scrollState1.scrollTo((scrollState1.value + dragAmount.toInt()).coerceIn(0, scrollState1.maxValue))
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
fun MedicationCard(medication: Medication, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${medication.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Time: ${medication.time}", style = MaterialTheme.typography.bodyMedium)
            Text("Day: ${medication.day}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onEdit) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}


// Other helper functions remain unchanged.


// Functions for saving and loading medications
fun saveMedications(sharedPreferences: SharedPreferences, medications: List<Medication>) {
    val editor = sharedPreferences.edit()
    val medicationJson = medications.joinToString("|") {
        "${it.id}#${it.name}#${it.time}#${it.day}"
    }
    editor.putString("medication_list", medicationJson)
    editor.apply()
}

fun loadMedications(sharedPreferences: SharedPreferences): List<Medication> {
    val medicationJson = sharedPreferences.getString("medication_list", "") ?: return emptyList()
    return medicationJson.split("|").mapNotNull {
        val parts = it.split("#")
        if (parts.size == 4) Medication(UUID.fromString(parts[0]), parts[1], parts[2], parts[3]) else null
    }
}



// Time Picker Dialog
@Composable
fun TimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        confirmButton = {
            TextButton(onClick = {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                onTimeSelected(timeFormat.format(calendar.time))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            AndroidView(
                factory = { ctx ->
                    android.widget.TimePicker(ctx).apply {
                        setIs24HourView(false)
                        setOnTimeChangedListener { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                        }
                    }
                }
            )
        }
    )
}


@Composable
fun PasswordDialog(
    onDismiss: () -> Unit,
    onPasswordEntered: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Password") },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onPasswordEntered(hashPassword1(password))
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )


}



