package com.example.library.utils

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object CoverDownloader {

    fun downloadBookCover(
        context: Context,
        bookTitle: String,
        coverId: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ) {

        Log.d("Download", "Начинаю загрузку обложки: $bookTitle, coverId: $coverId")

        if(!PermissionHelper.hasStoragePermission(context)) {
            Log.e("Download", "Нет разрешения на хранилище")
            onError("Необходимо разрешение на запись в хранилище")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("Download", "Загружаю изображение из сети")
                val imageUrl = "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
                val imageData = downloadImage(imageUrl)

                val fileName = createFileName(bookTitle, coverId)
                val filePath = saveImageToDownloads(context, imageData, fileName)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Обложка сохранена: $fileName",
                        Toast.LENGTH_LONG
                    ).show()
                    onSuccess(filePath)
                }
            }
            catch (e: Exception) {
               withContext(Dispatchers.Main) {
                   Log.e("Download", "Ошибка загрузки: ${e.message}", e)
                   val errorMsg = when {
                       e.message?.contains("Permission") == true ->
                           "Нет разрешения на запись файлов"
                       e.message?.contains("connect") == true ->
                           "Ошибка подключения. Проверьте интернет"
                       else -> "Ошибка загрузки: ${e.message}"
                   }

                   onError(errorMsg)
                   Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
               }
            }
        }
    }

    private fun downloadImage(imageUrl: String): ByteArray {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpsURLConnection

        connection.apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            doInput = true
        }
        return try {
            connection.inputStream.use {
                inputStream ->
                inputStream.readBytes()
            }
        }
        finally {
            connection.disconnect()
        }
    }

    private fun createFileName(bookTitle: String, coverId: Int): String {
        val cleanTitle = bookTitle
            .replace("[^a-zA-Z0-9а-яА-ЯёЁ]".toRegex(), "")
            .take(30)
            .ifEmpty { "book" }

        val timeStamp = System.currentTimeMillis()
        return "${cleanTitle}_${coverId}_${timeStamp}.jpg"
    }

    private fun saveImageToDownloads(context: Context, imageData: ByteArray, fileName: String): String {
        // Вариант 1: В общую папку Pictures (видно в галерее)
        val picturesDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "BookCovers"
        )

        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
            Log.d("Download", "📁 Создана папка Pictures/BookCovers")
        }

        val file = File(picturesDir, fileName)

        FileOutputStream(file).use { output ->
            output.write(imageData)
            output.flush()
        }

        Log.d("Download", "✅ Файл сохранен в галерею: ${file.absolutePath}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
        } else {
            context.sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())
                )
            )
        }

        return file.absolutePath
    }

    fun isCoverAvailable(coverId: Int?): Boolean {
        return coverId != null && coverId > 0
    }
}