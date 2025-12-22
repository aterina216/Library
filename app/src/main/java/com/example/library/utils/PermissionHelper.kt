package com.example.library.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionHelper {

    fun hasDownLoadPermission(context: Context): Boolean {

        val permissions = getRequiredPermissions()
        return permissions.all {
            permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }

    }

    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf<String>()

        // Для Android 13+ нужны новые разрешения вместо WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ используем READ_MEDIA_IMAGES для сохранения картинок
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Для старых версий оставляем WRITE_EXTERNAL_STORAGE
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return permissions.toTypedArray()
    }

    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ проверяем READ_MEDIA_IMAGES
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Для старых версий проверяем WRITE_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        else {
            true
        }
    }
    fun showPermissionRationale(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Требуется разрешение")
            .setMessage("Для сохранения обложки на устройство необходимо разрешение на запись в хранилище. Без этого скачивание невозможно.")
            .setPositiveButton("Предоставить разрешение") { _, _ ->
                // Открываем настройки приложения для ручной выдачи разрешения
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}