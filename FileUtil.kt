// FileUtil.kt
package com.example.dementia_app

import android.content.Context

import android.net.Uri


object FileUtil {
    fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(android.provider.MediaStore.Audio.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
            if (cursor.moveToFirst()) return cursor.getString(columnIndex)
        }
        return null
    }
}

