package com.example.library.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.registerForAllProfilingResults
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.library.R
import com.example.library.data.database.entity.BookEntity
import com.example.library.data.models.Book
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookCard(book: BookEntity,
             onBookClick: (String) -> Unit) {

    val subjectsList = remember(book.subjects) {
        book.subjects?.split(",")?.map { it.trim() } ?: emptyList()
    }

    val authorsList = remember(book.authors) {
        book.authors.split(",").map { it.trim() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                val workId = book.id.split("/").last()
                onBookClick(workId)
            }
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Обложка книги
            Box(
                modifier = Modifier
                    .width(90.dp)  // Ширина меньше высоты для книжной формы
                    .height(130.dp) // Высота больше ширины
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (book.coverId != null) {
                    AsyncImage(
                        model = "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            blendMode = BlendMode.Softlight
                        )
                    )
                } else {
                    Text(
                        text = "📚",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }

            // Информация о книге
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Название
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // Авторы
                if (authorsList.isNotEmpty()) {
                    Text(
                        text = authorsList.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Год издания
                if (book.firstPublishYear != null) {

                    Box(modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )) {

                        Text(
                            text = "📅 ${book.firstPublishYear}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Жанры (теги) - ТОЛЬКО если есть
                if (subjectsList.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        maxItemsInEachRow = 3
                    ) {
                        subjectsList.take(3).forEach { subject ->
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = subject.trim(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (subjectsList.size > 3) {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${subjectsList.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

