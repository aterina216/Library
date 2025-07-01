package com.example.library.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.library.api.getBooksWithDescription
import com.example.library.data.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewModelScope
import com.example.library.data.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookViewModel(private val repo: BookRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    private var job: Job? = null

    // Используем viewModelScope для запуска корутины
    fun loadBooks() {
        job = viewModelScope.launch {
            try {
                // Загружаем книги из репозитория
                val booksList = repo.loadBooks()
                // Обновляем LiveData с полученными книгами
                _books.value = booksList
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading books", e)
            }
        }
    }
}