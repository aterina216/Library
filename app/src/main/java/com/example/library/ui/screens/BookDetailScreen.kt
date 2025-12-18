package com.example.library.ui.screens

import android.R
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.library.ui.components.BadgeItem
import com.example.library.ui.components.ShelfCategoryItem
import com.example.library.ui.components.SimpleDetailRow
import com.example.library.ui.viewmodels.BookViewModel
import kotlin.collections.mapOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    navController: NavController,
    viewModel: BookViewModel
) {

    var showShelfMenu by remember { mutableStateOf(false) }

    val currentBook by viewModel.currentBook.collectAsState()

    val currentShelfStatus by viewModel.currentBookShelfStatus.collectAsState()
    val scrollState = rememberScrollState()



    LaunchedEffect(bookId) {
        viewModel.openBookById(bookId) // ✅ Просто загружаем каждый раз
    }

    LaunchedEffect(currentShelfStatus) {
        // Логируем для отладки
        Log.d("BookDetailScreen", "Статус книги обновлен: $currentShelfStatus")
    }


    if(currentBook == null) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Загружаем книгу...", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        return
    }

    val book = currentBook!!

    val coverId = book.covers.firstOrNull()
    val imageUrl = remember(coverId) {
        if (coverId != null) "https://covers.openlibrary.org/b/id/${coverId}-L.jpg" else null
    }

    val descriptionText = remember(book.description) {
        when (book.description){
            is String -> book.description
            is Map<*, *> -> book.description["value"]as? String?
            else -> ""
        }
    }

    val authorsText = remember(book.authors) {
        book.authors.joinToString(", ") {
            author -> author.author.key.substringAfterLast("/")
        }
    }


    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        "Детали книги",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.25).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = 600f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Верхняя секция с обложкой и градиентом
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = 250f
                            )
                        )
                ) {
                    // Декоративные элементы фона
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        Color.Transparent
                                    ),
                                    radius = 400f
                                )
                            )
                    )

                    // Обложка книги с эффектом тени
                    Card(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(10.dp),
                                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Обложка книги",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Основной контент
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    // Заголовок (теперь без сердечка)
                    Text(
                        text = book.title ?: "Без названия",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            lineHeight = 34.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Автор
                    Text(
                        text = authorsText ?: "Неизвестный автор",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )

                    // Бейджи с метаинформацией
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (book.subjects.isNotEmpty()) {
                            BadgeItem(
                                icon = "🏷️",
                                text = "${book.subjects.size} жанров",
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        // Если есть места
                        if (book.subject_places.isNotEmpty()) {
                            BadgeItem(
                                icon = "📍",
                                text = "${book.subject_places.size} мест",
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        // Если есть люди
                        if (book.subject_people.isNotEmpty()) {
                            BadgeItem(
                                icon = "👥",
                                text = "${book.subject_people.size} персонажей",
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    // Кнопка добавления на полку (без иконок, равномерный фон)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        // Основная кнопка
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showShelfMenu = true },
                            shape = MaterialTheme.shapes.extraLarge,
                            color = when (currentShelfStatus) {
                                "want_to_read" -> MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.8f
                                )

                                "reading" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                "read" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            },
                            tonalElevation = if (currentShelfStatus != null) 1.dp else 2.dp,
                            border = BorderStroke(
                                width = 1.dp,
                                color = when (currentShelfStatus) {
                                    "want_to_read" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    "reading" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                    "read" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (currentShelfStatus) {
                                            "want_to_read" -> "📝 Хочу прочитать"
                                            "reading" -> "📖 Читаю"
                                            "read" -> "✅ Прочитано"
                                            else -> "➕ Добавить на книжную полку"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = when (currentShelfStatus) {
                                            "want_to_read" -> MaterialTheme.colorScheme.primary
                                            "reading" -> MaterialTheme.colorScheme.secondary
                                            "read" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.onPrimary
                                        }
                                    )

                                    Icon(
                                        imageVector = Icons.Outlined.ArrowDropDown,
                                        contentDescription = null,
                                        tint = when (currentShelfStatus) {
                                            "want_to_read" -> MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.7f
                                            )

                                            "reading" -> MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.7f
                                            )

                                            "read" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                                            else -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        }
                                    )
                                }
                            }
                        }

                        // Выпадающее меню (без иконок)
                        DropdownMenu(
                            expanded = showShelfMenu,
                            onDismissRequest = { showShelfMenu = false },
                            modifier = Modifier
                                .width(280.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            // Заголовок меню
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Добавить на полку",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Выберите категорию",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )

                            // Варианты категорий (без иконок)
                            ShelfCategoryItem(
                                title = "📝 Хочу прочитать",
                                description = "Для будущего чтения",
                                isSelected = currentShelfStatus == "want_to_read",
                                onClick = {
                                    viewModel.addBookToShelf("want_to_read")
                                    showShelfMenu = false
                                }
                            )

                            ShelfCategoryItem(
                                title = "📖 Читаю",
                                description = "Сейчас читаю",
                                isSelected = currentShelfStatus == "reading",
                                onClick = {
                                    viewModel.addBookToShelf("reading")
                                    showShelfMenu = false
                                }
                            )

                            ShelfCategoryItem(
                                title = "✅ Прочитано",
                                description = "Уже прочитал",
                                isSelected = currentShelfStatus == "read",
                                onClick = {
                                    viewModel.addBookToShelf("read")
                                    showShelfMenu = false
                                }
                            )

                            // Кнопка удаления, если книга уже добавлена
                            if (currentShelfStatus != null) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )

                                TextButton(
                                    onClick = {
                                        viewModel.removeBookFromShelf()
                                        showShelfMenu = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "❌ Удалить с полки",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Карточка с описанием
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Описание",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text(
                                text = descriptionText ?: "Нет описания",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp,
                                    letterSpacing = 0.15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Декоративный градиент вверху при скролле
            if (scrollState.value > 50) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
    }
}





