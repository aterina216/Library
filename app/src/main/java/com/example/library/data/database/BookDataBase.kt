package com.example.library.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity

@Database(entities = [BookEntity::class], exportSchema = false, version = 3)
abstract class BookDataBase: RoomDatabase() {

    abstract fun getDao(): BookDao

    companion object {

        fun getDatabase(context: Context): BookDataBase {
            return Room.databaseBuilder(
                context,
                BookDataBase::class.java,
                "books_db"
            ).build()
        }
    }
}