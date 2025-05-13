package com.example.dementia_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.PendingIntent

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val sharedPreferences = context.getSharedPreferences("Medications", Context.MODE_PRIVATE)
            val medications = loadMedications(sharedPreferences)
            medications.forEach {
                MedicationNotificationManager.scheduleMedicationNotification(context, it.name, it.time)
            }
            return // Exit function after rescheduling
        }

        val medicationName = intent?.getStringExtra("medication_name") ?: "Medication"
        showNotification(context, medicationName)
    }

    private fun showNotification(context: Context, medicationName: String) {
        val channelId = "medication_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8+ (Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medication Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder for scheduled medications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open app when notification is clicked
        val intent = Intent(context, MedicationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create PendingIntent to launch the activity when the notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this icon exists
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take: $medicationName")
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent) // Set the pending intent
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Notify with a unique ID based on medication name hash
        notificationManager.notify(medicationName.hashCode(), notification)
    }
}