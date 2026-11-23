package com.futsalmatch.app.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageStorageUtil {
    fun copyUriToFilesDir(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "team_photos")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "team_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
