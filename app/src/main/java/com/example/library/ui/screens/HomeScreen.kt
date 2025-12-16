@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.library.ui.screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.util.copy
import com.example.library.ui.BookCategory
import com.example.library.ui.components.BookCard
import com.example.library.ui.components.CategoryTabs
import com.example.library.ui.viewmodels.BookViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import java.net.URLDecoder

@SuppressLint("SuspiciousIndentation", "RememberReturnType")
@Composable
fun HomeScreen(viewModel: BookViewModel, navController: NavController) {
    val searchBooks by viewModel._searchBooks.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val hasMoreSearch by viewModel.hasMoreSearch.collectAsState()
    val isLoadingMoreSearch by viewModel.isLoadingMoreSearch.collectAsState()

    val categories = BookCategory.entries
    val currentCategory by viewModel.currentCategory.collectAsState()
    val currentBooks by viewModel.currentBooks.collectAsState()
    val listState = rememberLazyListState()
    val isLoading by viewModel._isLoading.collectAsState()
    val hasMore by viewModel._hasMore.collectAsState()
    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(searchText) {
        if (searchText.isNotBlank()) {
            delay(500)
            Log.d("HomeScreen", "⏱️ Дебаунс сработал, ищем: '$searchText'")
            viewModel.searchBooks(searchText)
        } else {
            viewModel.searchBooks("")
        }
    }

    LaunchedEffect(listState, searchText, isSearching, hasMoreSearch) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleIndex >= totalItems - 3 && totalItems > 0
        }.collect {
            shouldLoadMore ->
            if(shouldLoadMore) {
                if (searchText.isNotEmpty() && !isSearching && hasMoreSearch){
                    Log.d("SearchPagination", "Догружаем следующую страницу поиска...")
                    viewModel.loadMoreSearch()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                viewModel.saveScrollPosition(currentCategory, index, offset)
            }
    }

    LaunchedEffect(listState, isLoading, hasMore, searchText) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleIndex >= totalItems - 3 && totalItems > 0
        }.collect {
            shouldLoadMore ->
            if(shouldLoadMore && !isLoading && hasMore && searchText.isEmpty() && !isSearching) {
                Log.d("Пагинация", "📖 Догружаем следующую страницу...")
                coroutineScope.launch {
                    viewModel.loadCategory(currentCategory)
                }
            }
        }
    }


    LaunchedEffect(currentCategory, currentBooks) {
        // маленькая задержка, чтобы LazyColumn успела отрисоваться
        delay(100)
        val (index, offset) = viewModel.getScrollPosition(currentCategory) ?: (0 to 0)
        if (index != 0 || offset != 0) {
            coroutineScope.launch {
                listState.scrollToItem(index, offset)
                Log.d(
                    "HomeScreen",
                    "✅ Восстановлена позиция: $index / $offset для ${currentCategory.displayName}"
                )
            }
        } else {
            Log.d("HomeScreen", "↩️ Новая категория — позиция с нуля")
        }
    }


    var filteredBooks = remember(currentBooks, searchBooks, searchText, isSearching) {
        if (searchText.isEmpty()) {
            currentBooks
        } else {
            if (isSearching) {
                emptyList()
            } else {
                searchBooks
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
        ), startY = 0f, endY = 1000f
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
                        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp
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
                            ), shape = RoundedCornerShape(50)
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
                            ), shape = RoundedCornerShape(16.dp)
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
                        ), contentAlignment = Alignment.CenterStart
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
                            })
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { searchText = "" }, modifier = Modifier.size(20.dp)
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

                if (searchText.isEmpty()) {
                    CategoryTabs(
                        categories = BookCategory.entries,
                        currentCategory = currentCategory,
                        onCategorySelected = { selected ->
                            if (selected != currentCategory) {
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }
                                viewModel.loadCategory(selected)
                            }
                        }
                    )
                }
            }
        })


    { PaddingValues ->

        if (searchText.isNotBlank() && isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
                .background(backgroundGradient)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Карточки книг
                items(filteredBooks) { book ->
                    BookCard(
                        book, onBookClick = { workId ->
                            navController.navigate("book_detail/$workId")
                        })
                }


                // Декоративный элемент в конце
                item {
                    if (isLoading||isLoadingMoreSearch ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else {
                        Text(
                            text = if (searchText.isNotEmpty()) {
                                if (hasMoreSearch) "" else "✨ Найдено ${searchBooks.size} книг"
                            } else {
                                if (hasMore) "" else "✨ Конец списка"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
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
    }
}