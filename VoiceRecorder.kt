package com.example.dementia_app

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var outputFile: String? = null


    private var mediaPlayer: MediaPlayer? = null

    fun playRecording(filePath: String, onCompletion: () -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.apply {
                setDataSource(filePath)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }


    fun startRecording(): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "REC_$timeStamp.3gp"

        // Use getExternalFilesDir for app-specific storage
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (storageDir?.exists() == false) {
            storageDir.mkdirs()
        }

        outputFile = "${storageDir?.absolutePath}/$fileName"
        Log.d("VoiceRecorder", "Recording to: $outputFile")

        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            outputFile = null
        }

        return outputFile
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}
