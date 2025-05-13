package com.example.dementia_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import java.io.IOException

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val audioPath = intent.getStringExtra("AUDIO_PATH")
        if (audioPath.isNullOrEmpty()) return

        try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                start()
            }
            mediaPlayer.setOnCompletionListener {
                it.release()
                Toast.makeText(context, "Reminder Played", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

