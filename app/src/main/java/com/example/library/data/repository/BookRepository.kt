package com.example.library.data.repository

import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.library.data.api.ApiBookService
import com.example.library.data.database.BookDataBase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.mapper.BookMapper.toEntity
import com.example.library.data.models.response.BookDetailResponse
import com.example.library.data.models.response.OpenLibraryResponse
import com.example.library.ui.BookCategory
import kotlinx.coroutines.flow.firstOrNull

class BookRepository(
    private val api: ApiBookService,
    private val db: BookDataBase
) {

    suspend fun loadBooksByCategory(
        category: BookCategory,
        pageSize: Int = 20, page: Int = 1
    ): List<BookEntity>? {
        return try {
            val offset = (page - 1) * pageSize
            val response =
                api.getBooksBySubject(category.subject, limit = pageSize, offset = offset)

            val books = response.works.map {
                it.toEntity().copy(category = category.subject)
            }

            // Сохраняем в БД (опционально)
            db.getDao().insertBooks(books)

            books
        } catch (e: Exception) {
            Log.d("repo", "${e.message}")
            db.getDao().getBooksByCategory(category.subject)
        }
    }

    suspend fun searchBooks(
        query: String,
        pageSize: Int = 20,
        page: Int = 1
    ): List<BookEntity> {
        Log.d("Repository", "🔍 Ищем книги по запросу: '$query'")

        try {
            if (query.isBlank() || query.length < 3) {
                Log.d("Repository", "📭 Пустой запрос, возвращаем пустой список")
                return emptyList()
            }

            val offset = (page - 1) * pageSize

            val searchResponse = api.getSearchResult(query, pageSize, offset)
            Log.d("Repository", "📊 API вернул ${searchResponse.numFound} результатов")
            val bookList = searchResponse.docs.mapNotNull { book ->
                try {
                    Log.d("Repository", "🔄 Маппим книгу: ${book.title ?: "без названия"}")
                    book.toEntity()
                } catch (e: Exception) {
                    Log.w("Repository", "⚠️ Не удалось смаппить книгу: $e")
                    null
                }
            }
            return bookList
        } catch (e: Exception) {
            Log.e("Repository", "❌ Ошибка поиска: ${e.message}")
            Log.e("Repository", "❌ Stacktrace:", e)  // Полный стектрейс
            return emptyList()
        }
    }

    suspend fun getBookById(bookId: String): BookDetailResponse? {

        try {
            val book = api.getBookById(bookId)
            return book
        }
        catch (e: Exception) {
            Log.e("repo", "${e.message}")
            return null
        }
    }

    suspend fun saveBookToShelf(book: BookEntity, status: String) {
        // ПРОВЕРЯЕМ, ЕСТЬ ЛИ КНИГА В БАЗЕ
        val existingBook = db.getDao().getById(book.id)

        if (existingBook == null) {
            // Если книги нет - ВСТАВЛЯЕМ новую с нужным статусом
            val newBook = book.copy(shelfStatus = status)
            db.getDao().insertBook(newBook)
            Log.d("Repository", "📚 Книга вставлена в БД: ${book.id}, статус: $status")
        } else {
            // Если книга есть - ОБНОВЛЯЕМ статус
            db.getDao().updateBookShelfStatus(bookID = book.id, status)
            Log.d("Repository", "📝 Статус обновлен: ${book.id}, статус: $status")
        }
    }

    suspend fun getBooksFromShelf(status: String): List<BookEntity>? {
        return db.getDao().selectBooksByShelfStatus(status)
    }

    suspend fun removeBookFromShelf(book: BookEntity, status: String?) {
        val existingBook = db.getDao().getById(book.id)

        if (existingBook != null) {
            db.getDao().updateBookShelfStatus(book.id, null)
            Log.d("Repository", "🗑️ Статус удален: ${book.id}")
        } else {
            Log.d("Repository", "⚠️ Книги нет в БД, нечего удалять")
        }
    }

    suspend fun getBookShelfStatus(bookId: String): String? {
        return db.getDao().getById(bookId)?.shelfStatus
    }
}