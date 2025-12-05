package com.example.library.di

import com.example.library.data.api.ApiBookService
import com.example.library.data.database.BookDataBase
import com.example.library.data.repository.BookRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(apiBookService: ApiBookService, db: BookDataBase): BookRepository {
        return BookRepository(apiBookService, db)
    }
}