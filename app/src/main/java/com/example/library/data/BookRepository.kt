package com.example.library.data

import android.util.Log
import com.example.library.api.getBooksWithDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookRepository {
    // Используем suspend функцию, чтобы загрузить книги асинхронно
    suspend fun loadBooks(): List<Book> {
        try {
            return getBooksWithDescription()  // Это предполагаемый метод, который должен загружать книги
        } catch (e: Exception) {
            Log.e("BookRepository", "Error loading books", e)
            return emptyList()  // Возвращаем пустой список в случае ошибки
        }
    }
}