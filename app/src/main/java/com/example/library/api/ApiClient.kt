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
        val books = mutableListOf<Book>()
        try {
            val url = URL("https://openlibrary.org/subjects/fiction.json?limit=1000")
            Log.d("API Request", "Fetching books from OpenLibrary...")

            val jsonResponse = url.readText()
            Log.d("API Response", jsonResponse)

            val jsonObject = JSONObject(jsonResponse)
            val works = jsonObject.getJSONArray("works")

            Log.d("API Response", "Found ${works.length()} books.")
            for (i in 0 until works.length()) {
                val book = works.getJSONObject(i)
                val title = book.getString("title")
                val author = book.getJSONArray("authors").getJSONObject(0).getString("name")
                val coverId = book.optString("cover_id")

                // Проверяем, содержит ли название книги русские символы
                // if (title.contains(Regex("[а-яА-Я]"))) {
                // Если книга на русском, добавляем её в список
                val coverUrl = if (coverId.isNotEmpty()) {
                    "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
                } else {
                    "https://via.placeholder.com/100x150?text=No+Cover"
                }

                val publishYear = book.optString("first_publish_year", "")
                books.add(Book(title, author, publishYear, coverUrl))
                //}
                /*    else {
                              Log.d("Book Info", "Book not in Russian: $title")
                          } */
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error fetching or parsing books", e)
        }

        return@withContext books
    }
}


