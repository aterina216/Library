package com.example.library.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.adapters.BookAdapter
import com.example.library.api.getRussianBooks
import com.example.library.data.Book
import com.example.library.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Инициализация адаптера для RecyclerView (пока пустой список)
        bookAdapter = BookAdapter(emptyList())
        recyclerView.adapter = bookAdapter

        // Загружаем популярные книги
        loadPopularBooks()
    }

    private fun loadPopularBooks() {
        lifecycleScope.launch {
            try {
                val books = getRussianBooks()  // Получаем список книг
                Log.d("Books Count", "Количество книг: ${books.size}")  // Логируем количество книг
                if (books.isNotEmpty()) {
                    bookAdapter = BookAdapter(books)  // Передаем книги в адаптер
                    recyclerView.adapter = bookAdapter  // Устанавливаем адаптер
                } else {
                    Toast.makeText(this@MainActivity, "Нет данных для отображения", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
            }
        }
    }
}