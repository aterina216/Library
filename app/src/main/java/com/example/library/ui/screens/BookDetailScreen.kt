package com.example.library.ui.screens

import android.Manifest
import android.R
import android.app.Activity
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.buildList
import kotlin.collections.mapOf


import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.mapper.BookMapper.getSafeDescription
import com.example.library.ui.components.DatePickerDialog
import com.example.library.ui.components.TimePickerDialog
import com.example.library.ui.states.DownloadState
import com.example.library.utils.BookSharingUtils.shareBook
import com.example.library.utils.DownloadCover.startDownload
import com.example.library.utils.FormatterDate.formatOpenLibraryDate
import com.example.library.utils.PermissionHelper
import com.example.library.utils.PermissionHelper.showPermissionRationale
import com.example.library.utils.Reminder.scheduleBookReminder
import java.time.LocalDate
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
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

    val context = LocalContext.current

    val downloadState = remember { DownloadState() }

    var fabVisible by remember { mutableStateOf(true) }
    var previousScroll by remember { mutableStateOf(0) }

    var visibleDateIcon by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectedDate by remember {mutableStateOf<LocalDate?>(null)}
    var selectedTime by remember {mutableStateOf<LocalTime?>(null)}

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("Download", "Результат разрешений: $permissions")
        val allGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        if (allGranted) {
            Log.d("Download", "Все разрешения получены, начинаю загрузку")
            currentBook?.let { book ->
                val coverId = book.covers.firstOrNull()
                if (coverId != null) {
                    startDownload(
                        context = context,
                        bookTitle = book.title ?: "Book",
                        coverId = coverId,
                        downloadState = downloadState
                    )
                } else {
                    downloadState.downLoadError = "Обложка недоступна"
                }
            }
        } else {
            Log.d("Download", "Не все разрешения предоставлены")
            showPermissionRationale(context)
            downloadState.downLoadError = "Разрешения не предоставлены"
        }
    }

    LaunchedEffect(currentShelfStatus) {
        when (currentShelfStatus) {
            "want_to_read" -> visibleDateIcon = true
            else -> visibleDateIcon = false
        }
    }

    LaunchedEffect(bookId) {
        viewModel.openBookById(bookId) // ✅ Просто загружаем каждый раз
    }

    LaunchedEffect(currentShelfStatus) {
        // Логируем для отладки
        Log.d("BookDetailScreen", "Статус книги обновлен: $currentShelfStatus")
    }

    LaunchedEffect(scrollState.value) {
        val currentScroll = scrollState.value
        val scrollingDown = currentScroll > previousScroll

        // Если скроллим вниз и проскроллили больше 50px - скрываем
        if (scrollingDown && currentScroll > 50) {
            fabVisible = false
        }
        // Если скроллим вверх - показываем
        else if (!scrollingDown) {
            fabVisible = true
        }

        previousScroll = currentScroll
    }


    if (currentBook == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
        book.getSafeDescription() ?: ""
    }

    val authorsText = remember(book.authors) {
        book.authors?.joinToString(", ") { author ->
            author.author.key.substringAfterLast("/")
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
        },
        floatingActionButton = {
            if (fabVisible) {
                Column(
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    androidx.compose.material3.ExtendedFloatingActionButton(
                        onClick = {
                            currentBook?.let { book ->
                                shareBook(context, book)
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Поделиться",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        text = {
                            Text(
                                "Поделиться",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
                    )

                    androidx.compose.material3.ExtendedFloatingActionButton(
                        onClick = {
                            Log.d(
                                "Download",
                                "Нажата кнопка загрузки, coverId = ${currentBook?.covers?.firstOrNull()}"
                            )
                            val coverId = currentBook?.covers?.firstOrNull()
                            if (coverId != null) {
                                if (PermissionHelper.hasStoragePermission(context)) {
                                    Log.d("Download", "Разрешения уже есть, начинаю загрузку")
                                    startDownload(
                                        context = context,
                                        bookTitle = currentBook!!.title ?: "Book",
                                        coverId = coverId,
                                        downloadState = downloadState
                                    )
                                } else {
                                    Log.d(
                                        "Download",
                                        "Запрашиваю разрешения"
                                    )// Просто запрашиваем разрешения
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                            context as Activity,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        )
                                    ) {
                                        showPermissionRationale(context)
                                    } else {
                                        permissionLauncher.launch(PermissionHelper.getRequiredPermissions())
                                    }
                                }
                            } else {
                                Log.d("Download", "Нет coverId, обложка недоступна")
                                Toast.makeText(context, "Обложка недоступна", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = com.example.library.R.drawable.download),
                                contentDescription = "Скачать",
                                modifier = Modifier.size(20.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                            )
                        },
                        text = {
                            Text(
                                "Скачать обложку",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
                    )

                    if (visibleDateIcon) {
                        androidx.compose.material3.ExtendedFloatingActionButton(
                            onClick = { showDatePicker = true },
                            icon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Напомнить",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = {
                                Text(
                                    "Установить напоминание",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.primary.copy(),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

        },
    ) { PaddingValues ->
        DatePickerDialog(
            showDialog = showDatePicker,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
                showTimePicker = true
            }
        )

        TimePickerDialog(
            showDialog = showTimePicker,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { time ->
                selectedTime = time

                if (selectedDate != null && currentBook != null) {

                    val success = scheduleBookReminder(context, selectedDate!!, selectedTime!!, currentBook!!, viewModel)

                    if (success) {
                        Toast.makeText(context, "Напоминание установлено!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Не удалось установить напоминание", Toast.LENGTH_SHORT).show()
                    }

                    selectedDate = null
                    selectedTime = null
                }
            }
        )

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
                    if (book.subjects.isNotEmpty()) {
                        var isExpanded by remember { mutableStateOf(false) }
                        val maxLines = 4 // Максимальное количество строк в свернутом состоянии

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🏷️ Темы и категории",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = (-0.25).sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Количество тегов
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${book.subjects.size}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Теги с автоматическим переносом и ограничением строк
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = if (!isExpanded) 150.dp else Dp.Unspecified) // Примерная высота 4 строк
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        maxItemsInEachRow = 5
                                    ) {
                                        book.subjects.forEach { subject ->
                                            // Определяем цвет тега по типу
                                            val tagColor = when {
                                                subject.contains("fiction", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

                                                subject.contains("author", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)

                                                subject.contains("english", ignoreCase = true) ||
                                                        subject.contains(
                                                            "welsh",
                                                            ignoreCase = true
                                                        ) ->
                                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)

                                                subject.contains("children", ignoreCase = true) ->
                                                    Color(0xFFFFF3E0) // Светло-оранжевый
                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            }

                                            val textColor = when {
                                                subject.contains("fiction", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.primary

                                                subject.contains("author", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.secondary

                                                subject.contains("english", ignoreCase = true) ||
                                                        subject.contains(
                                                            "welsh",
                                                            ignoreCase = true
                                                        ) ->
                                                    MaterialTheme.colorScheme.tertiary

                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(tagColor)
                                                    .border(
                                                        width = 1.dp,
                                                        color = textColor.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(16.dp)
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = subject,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = textColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    // Затемнение для намека на скрытый контент
                                    if (!isExpanded) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            MaterialTheme.colorScheme.surface.copy(
                                                                alpha = 0.7f
                                                            ),
                                                            MaterialTheme.colorScheme.surface
                                                        ),
                                                        startY = 0f,
                                                        endY = 500f
                                                    )
                                                )
                                        )
                                    }
                                }

                                // Кнопка разворачивания/сворачивания
                                if (book.subjects.size > 20) { // Показывать кнопку только если тегов много
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TextButton(
                                            onClick = { isExpanded = !isExpanded },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (isExpanded) "Свернуть" else "Показать все",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Статистика по типам тегов
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Подсчитываем количество по категориям
                                    val fictionCount = book.subjects.count {
                                        it.contains("fiction", ignoreCase = true)
                                    }
                                    val authorsCount = book.subjects.count {
                                        it.contains("author", ignoreCase = true)
                                    }
                                    val languagesCount = book.subjects.count { subject ->
                                        subject.contains("english", ignoreCase = true) ||
                                                subject.contains("welsh", ignoreCase = true) ||
                                                subject.contains("french", ignoreCase = true) ||
                                                subject.contains("spanish", ignoreCase = true)
                                    }

                                    if (fictionCount > 0) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "📚",
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = "$fictionCount",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "жанры",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                    }

                                    if (authorsCount > 0) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "✍️",
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = "$authorsCount",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = "авторы",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                    }

                                    if (languagesCount > 0) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "🌐",
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = "$languagesCount",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                            Text(
                                                text = "языки",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                    }

                                    // Общее количество
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "🏷️",
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "${book.subjects.size}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "всего",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (book.subject_people.isNotEmpty()) {
                        var isExpandedPeople by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "👥 Персонажи книги",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = (-0.25).sp
                                        ),
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.1f
                                                )
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${book.subject_people.size}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Теги персонажей с автопереносом
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = if (!isExpandedPeople) 120.dp else Dp.Unspecified)
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        maxItemsInEachRow = 3
                                    ) {
                                        book.subject_people.forEach { person ->
                                            // Очищаем имя от лишней информации
                                            val cleanPerson =
                                                person.replace(Regex("\\(.*?\\)"), "").trim()

                                            // Определяем цвет в зависимости от типа персонажа
                                            val backgroundColor = when {
                                                person.contains(
                                                    "Fictitious character",
                                                    ignoreCase = true
                                                ) ->
                                                    MaterialTheme.colorScheme.tertiaryContainer

                                                person.contains(
                                                    "Historical figure",
                                                    ignoreCase = true
                                                ) ->
                                                    MaterialTheme.colorScheme.secondaryContainer

                                                person.contains("Author", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.primaryContainer

                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(
                                                    alpha = 0.5f
                                                )
                                            }

                                            val textColor = when {
                                                person.contains(
                                                    "Fictitious character",
                                                    ignoreCase = true
                                                ) ->
                                                    MaterialTheme.colorScheme.onTertiaryContainer

                                                person.contains(
                                                    "Historical figure",
                                                    ignoreCase = true
                                                ) ->
                                                    MaterialTheme.colorScheme.onSecondaryContainer

                                                person.contains("Author", ignoreCase = true) ->
                                                    MaterialTheme.colorScheme.onPrimaryContainer

                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }

                                            Card(
                                                modifier = Modifier,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = backgroundColor
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 1.dp
                                                )
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Иконка для типа персонажа
                                                    val icon = when {
                                                        person.contains(
                                                            "Fictitious character",
                                                            ignoreCase = true
                                                        ) -> "👤"

                                                        person.contains(
                                                            "Historical figure",
                                                            ignoreCase = true
                                                        ) -> "👑"

                                                        person.contains(
                                                            "Author",
                                                            ignoreCase = true
                                                        ) -> "✍️"

                                                        person.contains(
                                                            "Detective",
                                                            ignoreCase = true
                                                        ) -> "🔍"

                                                        person.contains(
                                                            "King",
                                                            ignoreCase = true
                                                        ) -> "👑"

                                                        person.contains(
                                                            "Queen",
                                                            ignoreCase = true
                                                        ) -> "👑"

                                                        else -> "👤"
                                                    }

                                                    Text(
                                                        text = icon,
                                                        fontSize = 14.sp,
                                                        modifier = Modifier.padding(end = 6.dp)
                                                    )

                                                    Text(
                                                        text = cleanPerson,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = textColor,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Затемнение для скрытого контента
                                    if (!isExpandedPeople) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            MaterialTheme.colorScheme.surface.copy(
                                                                alpha = 0.8f
                                                            )
                                                        ),
                                                        startY = 0f,
                                                        endY = 330f
                                                    )
                                                )
                                        )
                                    }
                                }

                                // Кнопка разворачивания
                                if (book.subject_people.size > 6) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TextButton(
                                            onClick = { isExpandedPeople = !isExpandedPeople },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.secondary
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (isExpandedPeople) "Свернуть" else "Показать всех",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = if (isExpandedPeople) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = if (isExpandedPeople) "Свернуть" else "Развернуть",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Добавить после карточки с персонажами, перед метаданными
                    if (book.subject_times.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            ),
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🕰️",
                                            fontSize = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Время действия",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = book.subject_times.joinToString(", "),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Индикатор количества (если есть несколько значений)
                                if (book.subject_times.size > 1) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                MaterialTheme.colorScheme.tertiary.copy(
                                                    alpha = 0.1f
                                                )
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${book.subject_times.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Добавить перед последним Spacer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Divider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Создание
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Создана",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                Text(
                                    text = formatOpenLibraryDate(book.created.value),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }

                            // Обновление
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Обновлена",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                Text(
                                    text = formatOpenLibraryDate(book.last_modified.value),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
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