package com.example.library.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.Book
import com.example.library.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class BookViewModel(private val repository: BookRepository): ViewModel() {

    private var books = MutableStateFlow<List<BookEntity>>(emptyList())
    val _books: StateFlow<List<BookEntity>> = books

    init {
        Log.d("viewmodel", "start")
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                val bookList = repository.loadBooks()
                Log.d("viewmodel", "${bookList?.size}")
                books.value = bookList ?: emptyList()
                Log.d("viewmodel", "StateFlow updated with ${_books.value.size} books")
            }
            catch (e: Exception) {
                Log.e("viewmodel", "${e.message}")
                books.value = emptyList()
            }
        }
    }
}