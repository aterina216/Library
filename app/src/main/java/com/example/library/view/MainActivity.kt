package com.example.library.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.adapters.BookAdapter
import com.example.library.api.ApiClient
import com.example.library.api.getBooksWithDescription
import com.example.library.data.Book
import com.example.library.data.BookRepository
import com.example.library.data.BookViewModelFactory
import com.example.library.databinding.ActivityMainBinding
import com.example.library.view.fragments.DetailBookFragment
import com.example.library.view.fragments.MyBookFragment
import com.example.library.view.fragments.SearchFragment
import com.example.library.viewmodels.BookViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookViewModel: BookViewModel
    private lateinit var repository: BookRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        repository = BookRepository()
        val factory = BookViewModelFactory(repository)

        // Получаем ViewModel через фабрику
        bookViewModel = ViewModelProvider(this, factory).get(BookViewModel::class.java)

        // Инициализация адаптера
        bookAdapter = BookAdapter(emptyList()) { book ->
            openBookDetailFragment(book)  // Открытие подробной информации о книге
        }
        recyclerView.adapter = bookAdapter

        // Наблюдаем за изменениями в данных
        bookViewModel.books.observe(this, Observer { books ->
            if (books.isNotEmpty()) {
                // Обновляем данные в адаптере
                bookAdapter.updateBooks(books)
                recyclerView.visibility = View.VISIBLE
            } else {
                // Показываем сообщение, если данных нет
                Toast.makeText(this@MainActivity, "Нет данных для отображения", Toast.LENGTH_SHORT).show()
            }
        })

        // Загружаем книги
        bookViewModel.loadBooks()

        // Обработка кликов на элементы BottomNavigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_my_books -> {
                    loadFragment(MyBookFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Открытие фрагмента с деталями книги
    private fun openBookDetailFragment(book: Book) {
        val fragment = DetailBookFragment.newInstance(book)

        if (supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment, fragment::class.java.simpleName)
                .addToBackStack(null)  // Добавляем в стек фрагментов
                .commit()
        }
    }

    // Загрузка нового фрагмента
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}