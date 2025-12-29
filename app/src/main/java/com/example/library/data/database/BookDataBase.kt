package com.example.library.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.utils.Reminder

@Database(entities = [BookEntity::class, BookReminderEntity::class], exportSchema = false, version = 5)
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