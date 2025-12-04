package com.example.library.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.models.Book
import com.example.library.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class BookViewModel(private val repository: BookRepository): ViewModel() {

    private var books = MutableStateFlow<List<Book>>(emptyList())
    val _books: StateFlow<List<Book>> = books

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                val bookList = repository.loadBooks()
                if (bookList != null) {
                    books.value = bookList.works
                    Log.d("viewmodel", "${bookList.works}")
                }
            }
            catch (e: Exception) {
                Log.e("viewmodel", "${e.message}")
            }
        }
    }
}