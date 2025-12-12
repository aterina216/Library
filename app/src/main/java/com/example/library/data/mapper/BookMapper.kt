package com.example.library.data.mapper

import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.Book
import com.example.library.data.models.SearchBook
import okhttp3.internal.notifyAll

object BookMapper {

    fun Book.toEntity(): BookEntity {
        return BookEntity(
            id = this.key,  // Берем key из Book

            title = this.title,

            // List<Author> -> String через запятую
            authors = this.authors.joinToString(", ") { it.name },

            coverId = this.cover_id,

            firstPublishYear = this.first_publish_year,

            // List<String> -> String через запятую
            subjects = this.subject.joinToString(", "),

            description = null  // В этом API нет описания в списке
        )
    }

    fun SearchBook.toEntity(): BookEntity {
        return BookEntity(
            id = this.key ?: "unknown_${System.currentTimeMillis()}", // если null - генерируем временный ID
            title = this.title ?: "Без названия",
            // List<String>? -> String через запятую или "Неизвестен"
            authors = this.author_name?.joinToString(", ") ?: "Неизвестен",
            coverId = this.cover_i,
            firstPublishYear = this.first_publish_year,
            subjects = null,
            description = null
        )
    }
}