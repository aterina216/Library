package com.example.library.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.library.data.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT * from books ")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE category = :category")
    suspend fun getBooksByCategory(category: String): List<BookEntity>


    @Query("UPDATE books SET shelf_status = :status WHERE id = :bookID")
    suspend fun updateBookShelfStatus(bookID: String, status: String?)

    @Query("SELECT * FROM books WHERE shelf_status = :status")
    suspend fun selectBooksByShelfStatus(status: String): List<BookEntity>
}