package com.example.dementia_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class ReminderScheduler {
    fun scheduleReminder(context: Context, audioPath: String, triggerTime: Long, requestCode: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("AUDIO_PATH", audioPath)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
