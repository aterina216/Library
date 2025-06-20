package com.example.library.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RoomDatabase
import com.example.library.R
import com.example.library.adapters.BooksAdapter
import com.example.library.api.ApiClient
import com.example.library.data.BookResponse
import com.example.library.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var booksAdapter: BooksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        booksAdapter = BooksAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = booksAdapter

        fetchBooks("kotlin")

    }

    private fun fetchBooks(query: String) {
        ApiClient.apiService.getBooks(query).enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    Log.d("API", "Response successful: ${response.body()}")
                    val books = response.body()?.docs ?: emptyList()

                    // Обновляем адаптер только после получения данных
                    if (books.isEmpty()) {
                        Toast.makeText(this@MainActivity, "No books found", Toast.LENGTH_SHORT).show()
                    }

                    booksAdapter.updateBooks(books) // Обновляем данные в адаптере
                } else {
                    Log.e("API", "Error: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    booksAdapter.updateBooks(emptyList()) // Показываем пустой список при ошибке
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Log.e("API", "Failure: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                booksAdapter.updateBooks(emptyList()) // Показываем пустой список при ошибке сети
            }
        })
    }
}