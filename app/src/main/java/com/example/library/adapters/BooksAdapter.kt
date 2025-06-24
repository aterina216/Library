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
import com.example.library.data.Book
import com.example.library.databinding.ItemBookBinding

class BookAdapter(private var books: List<Book>,
                  private val onClick: (Book) -> Unit) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.apply {
            titleTextView.text = book.title
            authorTextView.text = book.author_name.joinToString(", ")
            publishYearTextView.text = "Year: ${book.first_publish_year ?: "Unknown Year"}"

            // Обработаем загрузку изображения
            val coverUrl = if (book.cover_i != null) {
                "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg"
            } else {
                "https://via.placeholder.com/100x150?text=No+Cover"
            }

            Glide.with(holder.itemView.context)
                .load(coverUrl)
                .placeholder(R.drawable.outline_book_5_24)
                .error(R.drawable.outline_book_5_24)
                .into(coverImageView)
        }
        holder.binding.root.setOnClickListener {
            onClick(book)
        }
    }

    override fun getItemCount(): Int = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)
}