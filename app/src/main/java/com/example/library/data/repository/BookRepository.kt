package com.example.library.data.repository

import android.util.Log
import com.example.library.data.api.ApiBookService
import com.example.library.data.database.entity.BookDataBase
import com.example.library.data.models.response.OpenLibraryResponse

class BookRepository(
    private val api: ApiBookService,
private val db: BookDataBase) {

    suspend fun loadBooks(): OpenLibraryResponse? {

        try {
            val books = api.getFictionBooks()
            if (books != null) {
                return books
            }
            else null
        }
        catch (e: Exception) {
            Log.e("Repository", "${e.message}")
            null
        }
         return null
    }
}