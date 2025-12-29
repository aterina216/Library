package com.example.library.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatterDate {

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatOpenLibraryDate(dateString: String?): String {
        if(dateString.isNullOrEmpty()) return "Неизвестно"

        return try {
            val date = java.time.LocalDateTime.parse(dateString,
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm")
            date.format(formatter).replaceFirstChar { it.uppercase() }
        }
        catch (e: Exception) {
            val yearMatch = Regex("\\b(\\d{4})\\b").find(dateString)
            yearMatch?.groupValues?.getOrNull(1) ?: dateString
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatYearOnly(dateString: String?): String {
        if(dateString.isNullOrEmpty()) return "???"

        return try {
            val date = java.time.LocalDateTime.parse(dateString,
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            date.year.toString()
        }
        catch (e: Exception) {
            val yearMatch = Regex("\\b(\\d{4})\\b").find(dateString)
            yearMatch?.groupValues?.getOrNull(1) ?: "???"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatReminderTime(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val timeFormat = SimpleDateFormat("HH:mm")
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val now = System.currentTimeMillis()
        val isToday = android.text.format.DateUtils.isToday(timeInMillis)
        val isTomorrow = android.text.format.DateUtils.isToday(timeInMillis - 86400000L)

        return when {
            isToday -> "Сегодня в ${timeFormat.format(date)}"
            isTomorrow -> "Завтра в ${timeFormat.format(date)}"
            else -> "${dateFormat.format(date)} в ${timeFormat.format(date)}"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatReminderDate(timeInMillis: Long): String {
        val date = Date(timeInMillis)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return dateFormat.format(date)
    }
}