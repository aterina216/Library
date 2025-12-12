package com.example.library.data.repository

import android.util.Log
import com.example.library.data.api.ApiBookService
import com.example.library.data.database.BookDataBase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.mapper.BookMapper.toEntity
import com.example.library.data.models.response.OpenLibraryResponse
import kotlinx.coroutines.flow.firstOrNull

class BookRepository(
    private val api: ApiBookService,
    private val db: BookDataBase
) {

    suspend fun loadBooks(): List<BookEntity>? {

        val cashedBooks = db.getDao().getAllBooks().firstOrNull()
        if (cashedBooks != null && cashedBooks.isNotEmpty()) {
            return cashedBooks
        }

        try {
            val books = api.getFictionBooks()
            if (books != null) {
                val booksEntity = books.works.map { book ->
                    book.toEntity()
                }

                db.getDao().insertBooks(booksEntity)
                return booksEntity
            } else null
        } catch (e: Exception) {
            Log.e("Repository", "${e.message}")
            null
        }
        return null
    }

    suspend fun searchBooks(query: String): List<BookEntity> {
        Log.d("Repository", "🔍 Ищем книги по запросу: '$query'")

        try {
            if(query.isBlank() || query.length < 3){
                Log.d("Repository", "📭 Пустой запрос, возвращаем пустой список")
                return emptyList()
            }
            val searchResponse = api.getSearchResult(query)
            Log.d("Repository", "📊 API вернул ${searchResponse.numFound} результатов")
            val bookList = searchResponse.docs.mapNotNull {book ->
                try {
                    Log.d("Repository", "🔄 Маппим книгу: ${book.title ?: "без названия"}")
                    book.toEntity()
                }
                catch (e: Exception) {
                    Log.w("Repository", "⚠️ Не удалось смаппить книгу: $e")
                    null
                }
            }
            return bookList
        }
        catch (e: Exception) {
            Log.e("Repository", "❌ Ошибка поиска: ${e.message}")
            Log.e("Repository", "❌ Stacktrace:", e)  // Полный стектрейс
            return emptyList()
        }
    }
}