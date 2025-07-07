package com.example.library.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {
    // Вставка новой книги или обновление существующей (по уникальному ключу)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    // Получение книги по уникальному ключу
    @Query("SELECT * FROM books WHERE key = :key LIMIT 1")
    suspend fun getBookByKey(key: String): Book?

    // Получение всех книг
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>
}
