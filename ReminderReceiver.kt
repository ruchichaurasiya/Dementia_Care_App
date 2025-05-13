package com.example.dementia_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val filePath = intent.getStringExtra("AUDIO_FILE_PATH")

        Toast.makeText(context, "Voice Reminder Triggered!", Toast.LENGTH_LONG).show()

        filePath?.let {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(it)
                mediaPlayer.prepare()
                mediaPlayer.start()

                // Optional: release player after playback completes
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to play reminder audio.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
