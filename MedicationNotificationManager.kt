package com.example.dementia_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

object MedicationNotificationManager {

    fun scheduleMedicationNotification(context: Context, medicationName: String, medicationTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 🔹 Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return // 🚨 Stop execution if permission is not granted
            }
        }

        // Convert time string (e.g., "10:30 AM") to Calendar object
        val calendar = Calendar.getInstance().apply {
            val parts = medicationTime.split(":| ".toRegex()).map { it.trim() }
            if (parts.size >= 3) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val isPM = parts[2].equals("PM", ignoreCase = true)

                set(Calendar.HOUR_OF_DAY, if (isPM && hour != 12) hour + 12 else if (!isPM && hour == 12) 0 else hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        // 15 minutes before notification
        val notifyBefore = calendar.clone() as Calendar
        notifyBefore.add(Calendar.MINUTE, -15)

        // Schedule both notifications
        scheduleAlarm(context, medicationName, notifyBefore.timeInMillis, 1001) // 15 min before
        scheduleAlarm(context, medicationName, calendar.timeInMillis, 1002) // Exact time
    }





    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(context: Context, medicationName: String, timeInMillis: Long, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            putExtra("medication_name", medicationName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    fun cancelMedicationNotification(context: Context, medicationName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            putExtra("medication_name", medicationName)
        }

        val pendingIntent1 = PendingIntent.getBroadcast(
            context, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pendingIntent2 = PendingIntent.getBroadcast(
            context, 1002, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent1)
        alarmManager.cancel(pendingIntent2)
    }

}


