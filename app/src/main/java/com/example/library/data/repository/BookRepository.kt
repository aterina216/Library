package com.example.library.data.repository

import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
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

    private val categoryCache = mutableMapOf<String, List<BookEntity>>()

    suspend fun loadBooksByCategory(category: BookCategory): List<BookEntity>? {

        if (categoryCache.containsKey(category.subject)) {
            Log.d("Repository", "📦 Загружаем из кэша памяти: ${category.displayName}")
            return categoryCache[category.subject]
        }

       return try {
            val response = api.getBooksBySubject(category.subject)
           val books = response.works.map { it.toEntity() }

           // Сохраняем в кэш
           categoryCache[category.subject] = books

           // Сохраняем в БД (опционально)
           db.getDao().insertBooks(books)

           books
        }
       catch (e: Exception) {
           Log.d("repo", "${e.message}")
           null
       }
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