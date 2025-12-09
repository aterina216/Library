@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.library.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.util.copy
import com.example.library.ui.components.BookCard
import com.example.library.ui.viewmodels.BookViewModel
import kotlinx.serialization.serializer
import java.net.URLDecoder

@SuppressLint("SuspiciousIndentation", "RememberReturnType")
@Composable
fun HomeScreen(viewModel: BookViewModel, navController: NavController) {
    val books by viewModel._books.collectAsState()

    var searchText by remember { mutableStateOf("") }

    var filteredBooks = remember(books, searchText) {
        if (searchText.isEmpty()) {
            books
        } else {
            books.filter { book ->
                book.title.contains(searchText, ignoreCase = true) ||
                        book.authors.contains(searchText, ignoreCase = true)
            }
        }
    }

    // Градиентный фон
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        ),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "📚 Моя библиотека",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${filteredBooks.size} книг в коллекции",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                // Декоративная линия
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .padding(vertical = 12.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            // Градиентный фон поля поиска
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            // Цвет границы - более темный фиолетовый
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "Поиск книг...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { searchText = "" },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Очистить поиск",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

        }
    )
    { PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
                .background(backgroundGradient)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Карточки книг
                items(filteredBooks) { book ->
                    BookCard(
                        book,
                        onBookClick = { workId ->
                            navController.navigate("book_detail/$workId")
                        }
                    )
                }


                // Декоративный элемент в конце
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Конец списка",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            // Легкое наложение градиента сверху для глубины
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}