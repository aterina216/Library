package com.example.library.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.library.data.models.response.BookDetailResponse

object BookSharingUtils {

    fun shareBook(context: Context, book: BookDetailResponse) {
        try {
            val shareText = buildShare(book)
            val shareIntent = createShareIntent(shareText)

            context.startActivity(Intent.createChooser(shareIntent, "Поделиться книгой"))
        }
        catch (e: Exception) {
            Toast.makeText(context, "Не удалось поделиться книгой", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildShare(book: BookDetailResponse): String {
        return buildString {
            append("📚 ${book.title ?: "Книга без названия"}\n\n")

            val description = extractDescription(book)
            description?.take(300)?.let {
                append("📝 $it...\n\n")
            }
            append("🔗 Открыть в OpenLibrary: https://openlibrary.org${book.key}")
        }
    }
    private fun extractDescription(book: BookDetailResponse): String? {
        return when (val desc = book.description) {
            is String -> desc
            is Map <*, *> -> desc["value"] as String?
            else -> null
        }
    }

    private fun createShareIntent(text: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
    }
}