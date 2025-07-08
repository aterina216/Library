package com.example.library.data

import android.util.Log
import com.example.library.api.ApiClient
import com.example.library.api.ApiService
import com.example.library.api.getBooksWithDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BookRepository(private val apiService: ApiService, private val bookDao: BookDao) {

    // Метод для загрузки книг с описанием
    suspend fun loadBooks(): List<Book> {
        // Сначала пытаемся получить данные из базы
        val cachedBooks = bookDao.getAllBooks()  // Получаем книги из базы данных
        if (cachedBooks.isNotEmpty()) {
            return cachedBooks  // Если книги есть в базе, возвращаем их
        }

        try {
            // Если в базе нет данных, делаем запрос к API
            val booksWithDescription = getBooksWithDescription()

            // Сохраняем книги с описанием в базу данных, если это необходимо
            booksWithDescription.forEach { book ->
                bookDao.insertBook(book)  // Вставляем или обновляем книгу в базе данных
            }

            return booksWithDescription
        } catch (e: Exception) {
            Log.e("BookRepository", "Error loading books", e)
            return emptyList()  // Возвращаем пустой список в случае ошибки
        }
    }

    // Функция для получения книг с описанием
    private suspend fun getBooksWithDescription(): List<Book> {
        val books = mutableListOf<Book>()
        try {
            // Получаем список книг
            val response = ApiClient.apiService.getBooks()
            if (!response.docs.isNullOrEmpty()) {
                // Параллельно получаем детали для каждой книги
                coroutineScope {
                    val deferredBooks = response.docs.map { book ->
                        async {
                            val bookId = book.key?.split("/")?.last()
                            val bookDetails = bookId?.let { ApiClient.apiService.getBookDetails(it) }

                            // Получаем описание
                            val descriptionText = when (val desc = bookDetails?.description) {
                                is String -> desc  // Если это строка
                                is Map<*, *> -> desc["text"] as? String ?: "Описание не доступно"  // Если это объект
                                else -> "Описание не доступно"  // Для других типов
                            }

                            book.description = descriptionText
                            book
                        }
                    }
                    // Ожидаем завершения всех асинхронных операций
                    books.addAll(deferredBooks.awaitAll())
                }
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error fetching or parsing books", e)
        }
        return books
    }
}

