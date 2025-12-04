package com.example.library.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.library.data.database.dao.BookDao
import com.example.library.data.database.entity.BookDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule(private  val app: Application) {

   @Singleton
   @Provides
   fun providesApp(): Application = app

    @Singleton
    @Provides
    fun providesContext(app: Application): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun provideDataBase(context: Context): BookDataBase {
        return Room.databaseBuilder(
            context,
            BookDataBase::class.java,
            "books_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(db: BookDataBase): BookDao {
        return db.getDao()
    }
}