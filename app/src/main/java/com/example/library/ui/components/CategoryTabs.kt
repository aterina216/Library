package com.example.library.ui.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.library.ui.BookCategory

@Composable
fun CategoryTabs(
    categories: List<BookCategory>,
    currentCategory: BookCategory,
    onCategorySelected: (BookCategory) -> Unit
) {
    val selectedIndex by remember(currentCategory) {
        derivedStateOf { categories.indexOf(currentCategory) }
    }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp,
        divider = {},
        indicator = {
            tabsPositions ->
            Box(modifier = Modifier
                .tabIndicatorOffset(tabsPositions[categories.indexOf(currentCategory)])
                .fillMaxWidth()
                .height(3.dp)
                .background(color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50))
            )
        },
    ) {
        categories.forEach {
            category ->
            Tab(
                selected = category == currentCategory,
                onClick = {onCategorySelected(category)},
                modifier = Modifier.padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (category == currentCategory)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                )
            }
        }
    }
}