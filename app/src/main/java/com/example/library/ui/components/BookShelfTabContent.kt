package com.example.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.library.data.database.entity.BookEntity
import com.example.library.ui.states.EmptyShelfState

@Composable
fun BookShelfTabContent (
    status: String,
    icon: String,
    message: String,
    books: List<BookEntity>
){
    if(books.isEmpty()) {
        EmptyShelfState(icon, message)
    }
    else {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            items(books) {
                book ->
                BookCard(
                    book,
                    onBookClick = { bookId ->
                        println("Нажата книга с ID: $bookId")
                    }
                )
            }
        }
    }
}