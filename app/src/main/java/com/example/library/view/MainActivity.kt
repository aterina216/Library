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
import com.example.library.view.fragments.HomeFragment
import com.example.library.view.fragments.MyBookFragment
import com.example.library.view.fragments.SearchFragment
import com.example.library.viewmodels.BookViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Загрузка HomeFragment при старте приложения
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Обработка кликов на элементы BottomNavigation
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment()) // Открытие HomeFragment
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

    // Загрузка нового фрагмента
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
