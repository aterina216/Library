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

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    private var booksLoaded = false
    private var isLoading = false

    // Загрузка книг
    fun loadBooks() {
        // Проверяем, не загружаются ли уже книги и не были ли они загружены
        if (booksLoaded || isLoading) return  // Не загружаем, если уже загружены или в процессе загрузки

        isLoading = true
        viewModelScope.launch {
            Log.d("BookViewModel", "loadBooks: Начинаем загрузку книг")
            try {
                // Вызываем метод загрузки книг с описанием
                val booksList = repository.loadBooks()
                _books.value = booksList
                booksLoaded = true
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading books", e)
                // Можно обработать ошибку здесь, например, показать Toast
            } finally {
                isLoading = false
            }
        }
    }

    // Очистка данных и сброс состояния
    fun clearBooks() {
        _books.value = emptyList()
        booksLoaded = false
    }
}

