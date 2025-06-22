package com.example.library.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class Book(
    val title: String,
    val author_name: List<String>,  // список авторов
    val first_publish_year: String?,
    val cover_i: Int?  // ID обложки
)

