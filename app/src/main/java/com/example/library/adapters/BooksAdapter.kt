package com.example.library.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.library.R
import com.example.library.api.getPopularBooks
import com.example.library.data.Book
import com.example.library.databinding.ItemBookBinding

class BookAdapter(private val books: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]  // Получаем книгу по позиции
        holder.binding.apply {
            titleTextView.text = book.title  // Название книги
            authorTextView.text = book.author_name  // Автор
            publishYearTextView.text = "Year: ${book.first_publish_year}"  // Год издания

            // Загружаем изображение с URL
            Glide.with(holder.itemView.context)
                .load(book.coverUrl)  // Загружаем картинку по URL
                .placeholder(R.drawable.outline_book_5_24)
                .error(R.drawable.outline_book_5_24)
                .into(coverImageView)
        }
    }

    override fun getItemCount(): Int = books.size  // Количество элементов в списке

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)
}
