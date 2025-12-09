package com.example.library.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.data.database.entity.BookEntity
import com.example.library.ui.components.BookShelfTabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookShelfScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("Хочу прочитать", "Читаю", "Прочитано")

    val exampleBooks = listOf(
        BookEntity(
            id = "/works/OL2732070W",
            title = "1984",
            authors = "Джордж Оруэлл",
            coverId = 10614930,
            firstPublishYear = 1949,
            subjects = "Дистопия, Политическая литература, Научная фантастика",
            description = null
        ),
        BookEntity(
            id = "/works/OL2766521W",
            title = "Мастер и Маргарита",
            authors = "Михаил Булгаков",
            coverId = 10614931,
            firstPublishYear = 1967,
            subjects = "Мистика, Сатира, Классическая литература",
            description = null
        )
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                    MaterialTheme.colorScheme.background
                )
            )
        ))
    {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(vertical = 12.dp))
        {
            Text(
                text = "📚 Книжная полка",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.CenterStart)
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            tabTitles.forEachIndexed {
                index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                        .border(
                            width = if (selectedTabIndex == index) 1.5.dp else 1.dp,
                            color = if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTabIndex = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else
                                FontWeight.Medium
                        ),
                        color = if (selectedTabIndex == index)
                        MaterialTheme.colorScheme.primary
                        else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        when (selectedTabIndex) {
            0 -> BookShelfTabContent(
                status = "Хочу прочитать",
                icon = "📝",
                message = "Книги, которые вы планируете прочитать",
                books = exampleBooks // Передаем примеры книг для этой вкладки
            )
            1 -> BookShelfTabContent(
                status = "Читаю",
                icon = "📖",
                message = "Книги, которые вы сейчас читаете",
                books = emptyList() // Пустой список для других вкладок
            )
            2 -> BookShelfTabContent(
                status = "Прочитано",
                icon = "✅",
                message = "Книги, которые вы уже прочитали",
                books = emptyList() // Пустой список для других вкладок
            )
        }
    }

}