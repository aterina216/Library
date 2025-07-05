package com.example.library.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.adapters.BookAdapter
import com.example.library.data.Book
import com.example.library.data.BookRepository
import com.example.library.data.BookViewModelFactory
import com.example.library.databinding.FragmentHomeBinding
import com.example.library.viewmodels.BookViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookViewModel: BookViewModel
    private lateinit var repository: BookRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        bookViewModel.books.observe(viewLifecycleOwner, Observer { books ->
            if (books.isNotEmpty()) {
                // Обновляем данные в адаптере
                bookAdapter.updateBooks(books)
                recyclerView.visibility = View.VISIBLE
            } else {
                // Показываем сообщение, если данных нет
                Toast.makeText(requireContext(), "Нет данных для отображения", Toast.LENGTH_SHORT).show()
            }
        })

        // Загружаем книги
        bookViewModel.loadBooks()
    }

    // Открытие фрагмента с деталями книги
    private fun openBookDetailFragment(book: Book) {
        val fragment = DetailBookFragment.newInstance(book)

        if (parentFragmentManager.findFragmentByTag(fragment::class.java.simpleName) == null) {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment, fragment::class.java.simpleName)
                .addToBackStack(null)  // Добавляем в стек фрагментов
                .commit()
        }
    }
}
