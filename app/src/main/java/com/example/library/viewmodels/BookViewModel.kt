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

    // Флаг, чтобы не загружать книги повторно, если они уже загружены
    private var booksLoaded = false

    fun loadBooks() {
        if (booksLoaded) return  // Если книги уже загружены, не загружаем снова

        viewModelScope.launch {
            try {
                val booksList = repository.loadBooks()
                _books.value = booksList
                booksLoaded = true
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading books", e)
            }
        }
    }
}