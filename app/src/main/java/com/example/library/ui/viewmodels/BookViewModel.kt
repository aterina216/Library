package com.example.library.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.Book
import com.example.library.data.repository.BookCategory
import com.example.library.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private var _booksByCategory = mutableStateMapOf<BookCategory, List<BookEntity>>()
    private var _currentCategory = MutableStateFlow(BookCategory.FICTION)
    val currentCategory: StateFlow<BookCategory> = _currentCategory

    private var _currentBooks = MutableStateFlow<List<BookEntity>>(emptyList())
    val currentBooks: StateFlow<List<BookEntity>> = _currentBooks

    private var searchBooks = MutableStateFlow<List<BookEntity>>(emptyList())
    val _searchBooks: StateFlow<List<BookEntity>> = searchBooks

    private var _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val loadedBooks = mutableStateMapOf<BookCategory, List<BookEntity>>()
    private val pageCounters = mutableStateMapOf<BookCategory, Int>()

    private var pageSize = 20
    private var isLoading = MutableStateFlow(false)
    val _isLoading: StateFlow<Boolean> = isLoading
    private var hasMore = MutableStateFlow(true)
    val _hasMore: StateFlow<Boolean> = hasMore



    init {
        Log.d("viewmodel", "start")
        loadCategory(BookCategory.FICTION)
    }

    fun loadCategory(category: BookCategory) {
        Log.d("ViewModel", "📂 Загружаем категорию: ${category.displayName}")

        _currentCategory.value = category

        val isNewCategory = pageCounters[category] == null
        if (isNewCategory) {
            pageCounters[category] = 0      // сброс страницы
            hasMore.value = true
            _currentBooks.value = emptyList()      // очистка только при смене категории
        }

        isLoading.value = true


        viewModelScope.launch {
            try {

                val currentPage = (pageCounters[category] ?: 0) + 1

                val books = repository.loadBooksByCategory(
                    category = category,
                    pageSize = pageSize,
                    page = currentPage
                ) ?: emptyList()

                pageCounters[category] = currentPage

                val existingBooks = loadedBooks[category] ?: emptyList()
                val allBooks = existingBooks + books
                loadedBooks[category] = allBooks

                _booksByCategory[category] = allBooks
                _currentCategory.value = category
                _currentBooks.value = books

                hasMore.value = books.size == pageSize
                Log.d("ViewModel", "✅ Загружено ${books.size} книг для ${category.displayName}")
            }
            catch (e: Exception) {
                Log.e("ViewModel", "❌ Ошибка загрузки ${category.displayName}: ${e.message}")
                _currentBooks.value = emptyList()
            } finally {
                isLoading.value = false
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
            } catch (e: Exception) {
                Log.e("ViewModel", "❌ Ошибка поиска в VM: ${e.message}", e)
                searchBooks.value = emptyList()
                _isSearching.value = false
            }
        }
    }
}