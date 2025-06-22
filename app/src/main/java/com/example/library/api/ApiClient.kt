package com.example.library.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.library.api.key.API_KEY
import com.example.library.data.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration


object ApiClient {
    private const val BASE_URL = "https://openlibrary.org/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

suspend fun getRussianBooks(): List<Book> {
    val books = mutableListOf<Book>()
    try {
        val response = ApiClient.apiService.getBooks()  // Получаем книги
        if (!response.docs.isNullOrEmpty()) {
            books.addAll(response.docs)
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching or parsing books", e)
    }
    return books
}


