package com.example.library.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.mapper.BookMapper.toEntity
import com.example.library.data.models.Book
import com.example.library.data.models.response.BookDetailResponse
import com.example.library.ui.BookCategory
import com.example.library.data.repository.BookRepository
import com.example.library.utils.Reminder.cancelBookReminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    // Текущая страница поиска
    private var searchPage = 1

    // Есть ли еще результаты для поиска
    private val _hasMoreSearch = MutableStateFlow(true)
    val hasMoreSearch: StateFlow<Boolean> = _hasMoreSearch

    private val searchCache = mutableMapOf<String, List<BookEntity>>()

    // Для отслеживания, загружаем ли мы следующую страницу поиска
    private val _isLoadingMoreSearch = MutableStateFlow(false)
    val isLoadingMoreSearch: StateFlow<Boolean> = _isLoadingMoreSearch

    private var currentSearchQuery = ""

    private val loadedBooks = mutableStateMapOf<BookCategory, List<BookEntity>>()
    private val pageCounters = mutableStateMapOf<BookCategory, Int>()

    private var pageSize = 20
    private var isLoading = MutableStateFlow(false)
    val _isLoading: StateFlow<Boolean> = isLoading
    private var hasMore = MutableStateFlow(true)
    val _hasMore: StateFlow<Boolean> = hasMore
    private val scrollPositions = mutableStateMapOf<BookCategory, Pair<Int, Int>>()

    private var _currentBook = MutableStateFlow<BookDetailResponse?>(null)
    val currentBook: StateFlow<BookDetailResponse?> = _currentBook

    private var _shelfBooks = MutableStateFlow<Map<String, List<BookEntity>?>>(emptyMap())
    val shelfBooks: StateFlow<Map<String, List<BookEntity>?>> = _shelfBooks

    private val _currentBookShelfStatus = MutableStateFlow<String?>(null)
    val currentBookShelfStatus: StateFlow<String?> = _currentBookShelfStatus

    private var booksInHistory = MutableStateFlow<List<BookEntity?>>(emptyList())
    val _booksInHistory: StateFlow<List<BookEntity?>> = booksInHistory

    private var _notifications = MutableStateFlow<List<BookReminderEntity>>(emptyList())
    val notifications: StateFlow<List<BookReminderEntity>> = _notifications


    init {
        /*Log.d("viewmodel", "start")*/
        loadCategory(BookCategory.FICTION)
    }

    fun loadCategory(category: BookCategory) {
        /*Log.d("ViewModel", "📂 Загружаем категорию: ${category.displayName}")*/
        _currentCategory.value = category

        val isNewCategory = pageCounters[category] == null
        if (isNewCategory) {
            pageCounters[category] = 0
            hasMore.value = true
            _currentBooks.value = loadedBooks[category] ?: emptyList()
        }

        if (isLoading.value) return
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
                _currentBooks.value = allBooks

                hasMore.value = books.size == pageSize

                /*Log.d("ViewModel", "✅ Загружено ${books.size} книг для ${category.displayName}")*/
            } catch (e: Exception) {
                /*Log.e("ViewModel", "❌ Ошибка загрузки ${category.displayName}: ${e.message}")*/
            } finally {
                isLoading.value = false
            }
        }
    }


    fun searchBooks(query: String) {
        /*Log.d("ViewModel", "🔍 Вызван поиск с запросом: '$query'")*/

        if (query.isBlank()) {
            /*Log.d("ViewModel", "📭 Пустой запрос, очищаем результаты")*/
            searchBooks.value = emptyList()
            _isSearching.value = false
            _hasMoreSearch.value = true
            return
        }

        if (query != currentSearchQuery) {
            currentSearchQuery = query
            searchPage = 1
            _hasMoreSearch.value = true
            searchBooks.value = emptyList()
        }

        _isSearching.value = true

        viewModelScope.launch {
            try {
                Log.d("ViewModel", "🔄 Запускаем поиск в репозитории")
                val books = repository.searchBooks(query, 20, searchPage)
                if (searchPage == 1) {
                    searchBooks.value = books
                } else {
                    val currentResults = searchBooks.value
                    searchBooks.value = currentResults + books
                }

                _hasMoreSearch.value = books.size == 20
                searchPage++
                Log.d(
                    "ViewModel",
                    "✅ Поиск: загружено ${books.size} книг, всего ${searchBooks.value.size}"
                )

            } catch (e: Exception) {
                Log.e("ViewModel", "❌ Ошибка поиска: ${e.message}")
                _hasMoreSearch.value = false
            } finally {
                _isSearching.value = false
                _isLoadingMoreSearch.value = false
            }
        }
    }

    fun loadMoreSearch() {
        if (!_hasMoreSearch.value || _isSearching.value || currentSearchQuery.isEmpty()) {
            return
        }
        _isLoadingMoreSearch.value = true

        viewModelScope.launch {
            try {
                Log.d(
                    "ViewModel",
                    "🔄 Догружаем еще для '$currentSearchQuery', страница $searchPage"
                )
                val books = repository.searchBooks(
                    query = currentSearchQuery,
                    pageSize = 20,
                    page = searchPage
                )

                val currentResults = searchBooks.value
                searchBooks.value = currentResults + books

                _hasMoreSearch.value = books.size == 20

                // Увеличиваем счетчик
                searchPage++

                Log.d(
                    "ViewModel",
                    "✅ Догружено ${books.size} книг, всего ${searchBooks.value.size}"
                )
            } catch (e: Exception) {
                Log.e("ViewModel", "❌ Ошибка догрузки: ${e.message}")
                _hasMoreSearch.value = false
            } finally {
                _isLoadingMoreSearch.value = false
            }
        }
    }

    fun saveScrollPosition(category: BookCategory, index: Int, offset: Int) {
        scrollPositions[category] = index to offset
    }

    fun getScrollPosition(category: BookCategory): Pair<Int, Int>? {
        return scrollPositions[category]
    }

    fun openBookById(bookId: String) {
        viewModelScope.launch {
            try {
                _currentBook.value = repository.getBookById(bookId)
                val status = repository.getBookShelfStatus(bookId)
                _currentBookShelfStatus.value = status

                // Сохраняем книгу в базу!
                _currentBook.value?.let { book ->
                    val bookEntity = book.toEntity()
                    repository.upsertBookForHistory(bookEntity)
                    booksInHistory.value = booksInHistory.value + bookEntity
                }

                Log.d("ViewModel", "✅ Книга сохранена и время обновлено: $bookId")

            } catch (e: Exception) {
                Log.e("viewmodel", "Ошибка: ${e.message}")
            }
        }
    }

    fun addBookToShelf(shelfStatus: String) {
        viewModelScope.launch {
            val currentBookDetail = _currentBook.value
            if (currentBookDetail != null) {
                val bookEntity = currentBookDetail.toEntity()

                Log.d("ViewModel", "➕ Добавляем книгу на полку: $shelfStatus")
                Log.d("ViewModel", "📖 ID книги: ${bookEntity.id}")

                repository.saveBookToShelf(bookEntity, shelfStatus)

                // Сразу обновляем состояние
                _currentBookShelfStatus.value = shelfStatus

                // Обновляем кэшированные списки полок
                val currentShelfBooks = _shelfBooks.value[shelfStatus] ?: emptyList()
                _shelfBooks.value = _shelfBooks.value + mapOf(
                    shelfStatus to (currentShelfBooks + bookEntity)
                )
            }
        }
    }

    fun removeBookFromShelf() {
        viewModelScope.launch {
            val currentBookDetail = _currentBook.value
            if (currentBookDetail != null) {
                val bookEntity = currentBookDetail.toEntity()

                Log.d("ViewModel", "🗑️ Удаляем книгу с полки")
                Log.d("ViewModel", "📖 ID книги: ${bookEntity.id}")

                repository.removeBookFromShelf(bookEntity, null)
                _currentBookShelfStatus.value = null

                // Удаляем книгу из всех кэшированных списков
                val updatedShelves = _shelfBooks.value.mapValues { (status, books) ->
                    books?.filter { it.id != bookEntity.id } ?: emptyList()
                }
                _shelfBooks.value = updatedShelves
            }
        }
    }

    fun loadAllShelves() {
        viewModelScope.launch {
            val statuses = listOf("want_to_read", "reading", "read")
            statuses.forEach { status ->
                val books = repository.getBooksFromShelf(status) ?: emptyList()
                _shelfBooks.value = _shelfBooks.value + mapOf(status to books)
                Log.d("ViewModel", "✅ Загружено ${books.size} книг для статуса: $status")
            }
        }
    }


    fun loadHistory() {
        viewModelScope.launch {
           try {
               booksInHistory.value = repository.getViewHistory()
           }
           catch (e: Exception) {
               Log.e("viewmodel", "${e.message}")
           }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                booksInHistory.value = emptyList()
                repository.clearHistory()
            }
            catch (e: Exception) {
                Log.e("viewmodel", "${e.message}")
            }
        }
    }

    fun addBookFromNotification(book: BookReminderEntity) {
        Log.d("BookViewModel", "➕ Вызван addBookFromNotification для: ${book.bookTitle}")

        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "📝 Начинаем сохранение напоминания...")
                val id = repository.addBookNotification(book)

                if(id>0) {
                    Log.d("BookViewModel", "✅ Напоминание успешно сохранено с ID: $id")

                    val currentList = _notifications.value.toMutableList()
                    currentList.add(book.copy(id = id))
                    _notifications.value = currentList
                    Log.d("BookViewModel", "📊 Обновленный список: ${_notifications.value.size} напоминаний")
                }
                else {
                    Log.e("BookViewModel", "❌ Напоминание не сохранено, ID: $id")
                }
            }
            catch (e: Exception) {
                Log.e("BookViewModel", "❌ Ошибка в addBookFromNotification: ${e.message}", e)
            }
        }
    }

    fun getAllNotifications() {
        viewModelScope.launch {
            try {
                val currenTime = System.currentTimeMillis()
                _notifications.value = repository.getAllNotitfications().filter {
                    it.notificationTime > currenTime
                }
            }
            catch (e: Exception) {
                Log.e("BookViewModel", "❌ Ошибка в getAllNotifications: ${e.message}", e)
            }
        }
    }

    fun deleteNotification(reminder: BookReminderEntity,  onCancelSystemNotification: (BookReminderEntity) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("BookViewModel", "🗑️ Удаляем напоминание: ${reminder.bookTitle}")
                onCancelSystemNotification(reminder)
                repository.deleteNotification(reminder)

                val updateList =_notifications.value.toMutableList()
                updateList.removeAll { it.id == reminder.id }
                _notifications.value = updateList


                Log.d("BookViewModel", "✅ Напоминание удалено")
            }
            catch (e: Exception) {
                Log.e("BookViewModel", "❌ Ошибка удаления напоминания: ${e.message}", e)
            }
        }
    }

    fun updateReminder(
        oldReminder: BookReminderEntity,
        newNotificationTime: Long,
        newNotificationId: Int
    ) {
        viewModelScope.launch {
            try {
                val updateReminder = oldReminder.copy(
                    notificationTime = newNotificationTime,
                    notificationId = newNotificationId,
                    createdAt = System.currentTimeMillis())

                repository.updateNotification(updateReminder)
                getAllNotifications()
                Log.d("BookViewModel", "Напоминание обновлено: ${oldReminder.id}")
            }
            catch (e: Exception) {
                Log.e("BookViewModel", "❌ Ошибка обновления напоминания", e)
            }
        }
    }
}