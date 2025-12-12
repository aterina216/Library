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

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private var books = MutableStateFlow<List<BookEntity>>(emptyList())
    val _books: StateFlow<List<BookEntity>> = books

    private var searchBooks = MutableStateFlow<List<BookEntity>>(emptyList())
    val _searchBooks: StateFlow<List<BookEntity>> = searchBooks

    private var _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching


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
            } catch (e: Exception) {
                Log.e("viewmodel", "${e.message}")
                books.value = emptyList()
            }
        }
    }

    fun searchBooks(query: String) {
        Log.d("ViewModel", "🔍 Вызван поиск с запросом: '$query'")

        if (query.isBlank()) {
            Log.d("ViewModel", "📭 Пустой запрос, очищаем результаты")
            searchBooks.value = emptyList()
            _isSearching.value = false
            return
        }

        viewModelScope.launch {
            try {
                Log.d("ViewModel", "🔄 Запускаем поиск в репозитории")
                val searchResponse = repository.searchBooks(query)
                Log.d("ViewModel", "📊 Репозиторий вернул: ${searchResponse.size} книг")
                searchBooks.value = searchResponse
                _isSearching.value = false

                Log.d("ViewModel", "✅ Обновили searchBooks: ${_searchBooks.value.size} книг")
            }
            catch (e: Exception) {
                Log.e("ViewModel", "❌ Ошибка поиска в VM: ${e.message}", e)
                searchBooks.value = emptyList()
                _isSearching.value = false
            }
        }
    }
}