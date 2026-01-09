package com.example.library.data.mapper

import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.AuthorForDetail
import com.example.library.data.models.Book
import com.example.library.data.models.SearchBook
import com.example.library.data.models.response.BookDetailResponse
import okhttp3.internal.notifyAll

object BookMapper {

    fun Book.toEntity(): BookEntity {
        return BookEntity(
            id = this.key,  // Берем key из Book

            title = this.title,

            // List<com.example.library.data.models.response.Author> -> String через запятую
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

    fun BookDetailResponse.toEntity(): BookEntity {
        val authorsText = if (this.authors.isNullOrEmpty()) {
            "Unknown author"
        } else {
            this.authors.joinToString(", ") {
                it.author?.key?.substringAfterLast("/") ?: it.author?.key ?: "Unknown"
            }
        }

        return BookEntity(
            id = extractBookId(this.key),
            title = this.title ?: "No title",
            /*authors = this.authors?.joinToString(", ") {
                it.author.key?.substringAfterLast("/") ?: "Unknown"
            } ?: "Unknown author",*/
            authors = authorsText,
            coverId = this.covers?.firstOrNull() ?: -1,
            firstPublishYear = null, // В BookDetailResponse нет года, можешь парсить из description если нужно
            subjects = this.subjects?.joinToString(", ") ?: "",
            description = when (val desc = this.description) {
                is String -> desc
                is Map<*, *> -> desc["value"] as? String
                else -> null
            },
            category = "",
            shelfStatus = null
        )
    }

    /*private fun extractBookId(key: String): String {
        return key.substringAfterLast("/").takeIf { it.isNotEmpty() } ?: "unknown"
    }*/

    private fun extractBookId(key: String): String {
        // Сначала удаляем URL параметры (всё после ?)
        val withoutParams = key.substringBefore("?")
        // Затем извлекаем ID после последнего слеша
        val id = withoutParams.substringAfterLast("/")
        // Проверяем, что ID не пустой
        return id.takeIf { it.isNotEmpty() } ?: "unknown"
    }

    private fun extractAuthors(authors: List<AuthorForDetail>): String {
        return if(authors.isNotEmpty()) {
            authors.mapNotNull {
                it.author?.key?.substringAfterLast("/")
            }.joinToString(", ")
        }
        else "Unklown author"
    }

    fun BookDetailResponse.getSafeDescription(): String? {
        return when (this.description) {
            is String -> this.description as String
            is Map<*, *> -> {
                val map = this.description as Map<*, *>
                (map["value"] as? String)
            }
            is com.google.gson.JsonObject -> {
                // Если используете Gson
                this.description.asJsonObject.get("value")?.asString
            }
            else -> null
        }
    }
}