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
}