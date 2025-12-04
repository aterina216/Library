package com.example.library.di

import com.example.library.data.repository.BookRepository
import com.example.library.ui.viewmodels.BookViewModel
import com.example.library.ui.viewmodels.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun providesViewModelFactory(repository: BookRepository): ViewModelFactory {
        return ViewModelFactory(repository)
    }

    @Singleton
    @Provides
    fun provideViewModel(repository: BookRepository): BookViewModel {
        return BookViewModel(repository)
    }
}