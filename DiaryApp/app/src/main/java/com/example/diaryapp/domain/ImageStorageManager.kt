package com.example.diaryapp.domain

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageStorageManager(private val context: Context) {

    suspend fun copyImageToInternalStorage(sourceUri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                val fileName = "img_${System.currentTimeMillis()}.jpg"
                val destFile = File(context.filesDir, "images/$fileName")
                destFile.parentFile?.mkdirs()
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    destFile.outputStream().use { output -> input.copyTo(output) }
                }
                destFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }

    fun deleteImage(path: String) {
        File(path).takeIf { it.exists() }?.delete()
    }
}
