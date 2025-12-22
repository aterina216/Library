package com.example.library.utils

import android.os.Build
import androidx.annotation.RequiresApi

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
}