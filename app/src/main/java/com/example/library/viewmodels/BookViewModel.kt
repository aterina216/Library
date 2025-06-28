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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {


    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    private var job: Job? = null

    fun loadBooks() {
        // Запуск корутины вручную с указанием Dispatchers.IO
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                val books = getBooksWithDescription()  // Загружаем книги с описаниями
                _books.value = books  // Обновляем LiveData
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error loading books", e)
            }
        }
    }
}