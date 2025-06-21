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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL



object ApiClient {
    private const val BASE_URL = "https://openlibrary.org/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
suspend fun getRussianBooks(): List<Book> {
    return withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()  // Список для хранения книг
        try {
            // Изменяем запрос, чтобы искать книги на русском языке
            val url = URL("https://openlibrary.org/subjects/fiction.json?limit=1000&language=rus")
            Log.d("API Request", "Fetching Russian books from OpenLibrary...")

            val jsonResponse = url.readText()  // Получаем ответ в формате JSON
            Log.d("API Response", jsonResponse)

            val jsonObject = JSONObject(jsonResponse)  // Парсим JSON-ответ
            val works = jsonObject.getJSONArray("works")  // Получаем массив "works" с книгами

            Log.d("API Response", "Found ${works.length()} books.")
            for (i in 0 until works.length()) {
                val book = works.getJSONObject(i)  // Получаем данные для каждой книги
                val title = book.getString("title")  // Название книги
                val author = book.getJSONArray("authors").getJSONObject(0).getString("name")  // Автор
                val coverId = book.optString("cover_id")  // ID обложки (если есть)

                Log.d("Book Info", "Title: $title, Author: $author")

                // Генерация URL для обложки
                val coverUrl = if (coverId.isNotEmpty()) {
                    "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
                } else {
                    "https://via.placeholder.com/100x150?text=No+Cover"  // Запасное изображение
                }

                val publishYear = book.optString("first_publish_year", "")  // Год издания книги

                // Добавляем книгу в список
                books.add(Book(title, author, publishYear, coverUrl))
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error fetching or parsing books", e)
        }

        return@withContext books  // Возвращаем список всех книг
    }
}