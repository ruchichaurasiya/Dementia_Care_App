package com.example.dementia_app

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random
import java.util.UUID

class VoiceAssistantActivity : ComponentActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showToast("Permission granted")
        } else {
            showToast("Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("VoiceAssistantPrefs", Context.MODE_PRIVATE)
        requestAudioPermission()

        setContent {
            MaterialTheme {
                VoiceAssistantScreen()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun requestAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }



    data class ReminderItem(val dateTime: String, val audioPath: String, val requestCode: Int)

    @Composable
    fun VoiceAssistantScreen(context: Context = LocalContext.current) {
        val recorder = remember { VoiceRecorder(context) }
        val player = remember { VoiceRecorder(context) }
        var isRecording by remember { mutableStateOf(false) }
        var isPlaying by remember { mutableStateOf(false) }
        var recordedFilePath by remember { mutableStateOf<String?>(null) }
        var selectedDate by remember { mutableStateOf<String?>(null) }
        var selectedDay by remember { mutableStateOf<String?>(null) }
        var selectedTime by remember { mutableStateOf<String?>(null) }
        var showPasswordDialog by remember { mutableStateOf(false) }
        var actionType by remember { mutableStateOf("set") }
        val scrollState2 = rememberScrollState()
        val coroutineScope2 = rememberCoroutineScope()


        val authPreferences = context.getSharedPreferences("CaregiverPrefs", Context.MODE_PRIVATE)
        val reminderList = remember { mutableStateListOf<ReminderItem>() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)) // Beige background
        )

        LaunchedEffect(Unit) {
            val savedSet = sharedPrefs.getStringSet("reminders", emptySet()) ?: emptySet()
            savedSet.forEach { entry ->
                val parts = entry.split("||")
                if (parts.size == 3) {
                    val dateTime = parts[0]
                    val audioPath = parts[1]
                    val requestCode = parts[2].toInt()
                    reminderList.add(ReminderItem(dateTime, audioPath, requestCode))
                }
            }
        }

        // Use vertical scroll for the whole content in Column
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState2)
                ) // Adding scroll
                {

                    // Recording button
                    Button(
                        onClick = {
                            if (!isRecording) {
                                recordedFilePath = recorder.startRecording()
                                if (recordedFilePath != null) {
                                    showToast("Recording started")
                                } else {
                                    showToast("Recording failed")
                                }
                            } else {
                                recorder.stopRecording()
                                showToast("Recording stopped")
                            }
                            isRecording = !isRecording
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRecording) "Stop Recording" else "Start Recording")
                    }

                    // Show recorded file path
                    recordedFilePath?.let {
                        Text(text = "Recorded file: $it", modifier = Modifier.padding(top = 8.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (!isPlaying) {
                                    player.playRecording(it) {
                                        isPlaying = false
                                    }
                                    isPlaying = true
                                } else {
                                    player.stopPlaying()
                                    isPlaying = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isPlaying) "Stop Playing" else "Play Recording")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Set Voice Reminder Section
                    Text(
                        text = "Set Voice Reminder",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("Please enter date or day based on your preference")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            pickDate(context) { date ->

                                selectedDate = date
                                selectedDay = null
                            }
                        }) {

                            Text("Pick Date")
                        }
                        Button(onClick = {
                            pickDay(context) { day ->
                                selectedDay = day
                                selectedDate = null
                            }
                        }) {
                            Text("Pick Day")
                        }
                    }

                    selectedDate?.let {
                        Text(text = "Selected Date: $it", modifier = Modifier.padding(top = 8.dp))
                    }
                    selectedDay?.let {
                        Text(text = "Selected Day: $it", modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pick Time button
                    Button(
                        onClick = { pickTime(context) { time -> selectedTime = time } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick Time")
                    }

                    selectedTime?.let {
                        Text(text = "Selected Time: $it", modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Set Reminder button
                    Button(
                        onClick = {
                            if (recordedFilePath == null) {
                                showToast("Please record a voice first.")
                                return@Button
                            }
                            if (selectedTime == null) {
                                showToast("Please select a time.")
                                return@Button
                            }

                            val (hour, minute) = selectedTime!!.split(":").map { it.toInt() }
                            val requestCode = Random.nextInt(100000)

                            if (selectedDate != null) {
                                val (year, month, day) = selectedDate!!.split("-")
                                    .map { it.toInt() }
                                scheduleOneTimeReminder(
                                    context,
                                    recordedFilePath!!,
                                    year,
                                    month - 1,
                                    day,
                                    hour,
                                    minute,
                                    requestCode
                                )

                                val reminderText = "$selectedDate $selectedTime"
                                val item =
                                    ReminderItem(reminderText, recordedFilePath!!, requestCode)
                                reminderList.add(item)
                                saveReminders(reminderList)
                            } else if (selectedDay != null) {
                                scheduleRepeatingDayReminder(
                                    context,
                                    recordedFilePath!!,
                                    selectedDay!!,
                                    hour,
                                    minute,
                                    requestCode
                                )

                                val reminderText = "$selectedDay $selectedTime"
                                val item =
                                    ReminderItem(reminderText, recordedFilePath!!, requestCode)
                                reminderList.add(item)
                                saveReminders(reminderList)
                            } else {
                                showToast("Please select a date or day.")
                                showPasswordDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Set Reminder")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Scheduled Reminders Section
                    Text(
                        text = "Scheduled Reminders",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (reminderList.isEmpty()) {
                        Text(text = "No reminders yet.")
                    } else {
                        reminderList.forEach { reminder ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Date/Time: ${reminder.dateTime}")
                                    Text(text = "Audio: ${reminder.audioPath.takeLast(20)}...")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = {
                                                player.playRecording(reminder.audioPath) { }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Play")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                cancelReminder(context, reminder.requestCode)
                                                reminderList.remove(reminder)
                                                saveReminders(reminderList)
                                                actionType = "delete"
                                                showPasswordDialog = true
                                                showToast("Reminder cancelled!")
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel All Reminders button
                    Button(
                        onClick = {
                            reminderList.forEach { cancelReminder(context, it.requestCode) }
                            reminderList.clear()
                            saveReminders(reminderList)
                            showToast("All reminders cancelled!")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel All Reminders")
                    }
                }

                CustomScrollbar2(scrollState2, coroutineScope2)

            }

        }


        

    }

    private fun pickDate(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$year-${month + 1}-$dayOfMonth")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickDay(context: Context, onDaySelected: (String) -> Unit) {
        val days = listOf("Everyday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        AlertDialog.Builder(context)
            .setTitle("Select Day")
            .setItems(days.toTypedArray()) { _, which ->
                onDaySelected(days[which])
            }
            .show()
    }

    private fun pickTime(context: Context, onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onTimeSelected("$hourOfDay:${String.format("%02d", minute)}")
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }



    @Composable
    fun CustomScrollbar2(scrollState2: ScrollState, coroutineScope2: CoroutineScope) {
        val coroutineScope = rememberCoroutineScope()
        val proportion = scrollState2.value.toFloat() / scrollState2.maxValue.toFloat()
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        coroutineScope2.launch {
                            scrollState2.scrollTo((scrollState2.value + dragAmount.toInt()).coerceIn(0, scrollState2.maxValue))
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



    private fun scheduleOneTimeReminder(
        context: Context,
        audioPath: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("AUDIO_FILE_PATH", audioPath)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        showToast("Reminder scheduled successfully!")
    }

    private fun scheduleRepeatingDayReminder(
        context: Context,
        audioPath: String,
        day: String,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {
        val daysMap = mapOf(
            "Sunday" to Calendar.SUNDAY,
            "Monday" to Calendar.MONDAY,
            "Tuesday" to Calendar.TUESDAY,
            "Wednesday" to Calendar.WEDNESDAY,
            "Thursday" to Calendar.THURSDAY,
            "Friday" to Calendar.FRIDAY,
            "Saturday" to Calendar.SATURDAY
        )

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("AUDIO_FILE_PATH", audioPath)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (day != "Everyday") {
                while (get(Calendar.DAY_OF_WEEK) != daysMap[day]) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }

        if (day == "Everyday") {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }

        showToast("Repeating reminder scheduled!")
    }

    private fun cancelReminder(context: Context, requestCode: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun saveReminders(reminders: List<ReminderItem>) {
        val set = reminders.map { "${it.dateTime}||${it.audioPath}||${it.requestCode}" }.toSet()
        sharedPrefs.edit().putStringSet("reminders", set).apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



