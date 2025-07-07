package com.example.library.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    // Преобразование List<String> в строку
    @TypeConverter
    fun fromAuthorList(authors: List<String>?): String? {
        return authors?.let { Gson().toJson(it) }
    }

    // Преобразование строки обратно в List<String>
    @TypeConverter
    fun toAuthorList(authors: String?): List<String>? {
        return authors?.let {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(it, type)
        }
    }
}