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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.example.library.data.database.entity.BookEntity
import com.example.library.ui.components.BookShelfTabContent
import com.example.library.ui.viewmodels.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookShelfScreen(viewModel: BookViewModel, navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabToStatusMap = listOf(
        "want_to_read" to "Хочу прочитать",
        "reading" to "Читаю",
        "read" to "Прочитано"
    )

    val shelfBooks by viewModel.shelfBooks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllShelves()
    }

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
        // Заголовок экрана
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

        // Табы
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            tabToStatusMap.forEachIndexed { index, (statusKey, titleText) ->  // ← Деструктурируем пару
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
                    // Внутри каждого таба должен быть свой текст, а не "Книжная полка"
                    val textColor = if (selectedTabIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }

                    Text(
                        text = titleText,  // ← Вот здесь должно быть название таба!
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = textColor
                    )
                }
            }
        }

        // Контент табов
        when (selectedTabIndex) {
            0 -> BookShelfTabContent(
                status = tabToStatusMap[0].second,
                icon = "📝",
                message = "Книги, которые вы планируете прочитать",
                books = shelfBooks[tabToStatusMap[0].first] ?: emptyList(),
                navController = navController,
            )
            1 -> BookShelfTabContent(
                status = tabToStatusMap[1].second,
                icon = "📖",
                message = "Книги, которые вы сейчас читаете",
                books = shelfBooks[tabToStatusMap[1].first] ?: emptyList(),
                navController = navController,
            )
            2 -> BookShelfTabContent(
                status = tabToStatusMap[2].second,
                icon = "✅",
                message = "Книги, которые вы уже прочитали",
                books = shelfBooks[tabToStatusMap[2].first] ?: emptyList(),
                navController
            )
        }
    }
}