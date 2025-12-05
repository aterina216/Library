package com.example.library.data.mapper

import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.Book

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
}