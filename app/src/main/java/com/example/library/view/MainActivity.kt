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
    private var books: List<Book> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Инициализация адаптера с пустым списком
        bookAdapter = BookAdapter(books) { book ->
            openBookDetailFragment(book)
        }
        recyclerView.adapter = bookAdapter

        // Загружаем книги
        loadPopularBooks()
    }

    override fun onResume() {
        super.onResume()
        // Восстанавливаем видимость RecyclerView, если он скрыт
        if (binding.recyclerView.visibility == View.GONE) {
            binding.recyclerView.visibility = View.VISIBLE
            Log.d("MainActivity", "RecyclerView восстановлен в onResume")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("MainActivity", "onBackPressed: Нажата кнопка назад")

        if (supportFragmentManager.backStackEntryCount == 0) {
            Log.d("MainActivity", "Возвращаемся на главный экран, показываем RecyclerView")
            binding.recyclerView.visibility = View.VISIBLE
            if (books.isNotEmpty()) {
                bookAdapter.notifyDataSetChanged()
                Log.d("MainActivity", "Адаптер обновлен")
            }
        }
    }

    private fun openBookDetailFragment(book: Book) {
        // Логируем процесс открытия фрагмента
        Log.d("MainActivity", "Открытие фрагмента: Скрытие RecyclerView")
        //binding.recyclerView.visibility = View.GONE

        val fragment = DetailBookFragment.newInstance(book)

        if (supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment, fragment::class.java.simpleName)
                .addToBackStack(null) // Добавляем в стек
                .commit()
            Log.d("MainActivity", "Фрагмент добавлен в транзакцию")
        } else {
            Log.d("MainActivity", "Фрагмент уже существует, не заменяем")
        }
    }

    private fun loadPopularBooks() {
        lifecycleScope.launch {
            try {
                books = getRussianBooks()
                Log.d("MainActivity", "Книги загружены: $books")

                if (books.isNotEmpty()) {
                    bookAdapter = BookAdapter(books) { book ->
                        openBookDetailFragment(book)
                    }
                    recyclerView.adapter = bookAdapter
                    binding.recyclerView.visibility = View.VISIBLE
                    Log.d("MainActivity", "RecyclerView показан")
                } else {
                    Toast.makeText(this@MainActivity, "Нет данных для отображения", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Ошибка при загрузке данных", e)
            }
        }
    }
}
