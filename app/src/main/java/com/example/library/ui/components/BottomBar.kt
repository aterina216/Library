package com.example.library.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DrawerDefaults.backgroundColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.library.R
import com.example.library.ui.theme.DarkBottomBar
import com.example.library.ui.theme.DarkOnBackground
import com.example.library.ui.theme.DarkPrimary
import com.example.library.ui.theme.DarkSurface
import com.example.library.ui.theme.LightBottomBar
import com.example.library.ui.theme.LightOnBackground
import com.example.library.ui.theme.LightPrimary
import com.example.library.ui.theme.LightSecondary
import com.example.library.ui.theme.LightSurface

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBottomBar else LightBottomBar
    val selectedColor = if (isDarkTheme) DarkPrimary else LightPrimary
    val unselectedColor = if (isDarkTheme) DarkOnBackground.copy(alpha = 0.6f)
    else LightOnBackground.copy(alpha = 0.6f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                icon = R.drawable.home,
                label = "Главная",
                isSelected = currentRoute == "home",
                onClick = { navController.navigate("home") },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )

            BottomBarItem(
                icon = R.drawable.history,
                label = "История",
                isSelected = currentRoute == "history",
                onClick = { navController.navigate("history") },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )

            BottomBarItem(
                icon = R.drawable.bookshelf,
                label = "Книжная полка",
                isSelected = currentRoute == "book_shelf",
                onClick = { navController.navigate("book_shelf") },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )

            BottomBarItem(
                icon = R.drawable.outline_alarm_24,
                label = "Напоминания",
                isSelected = currentRoute == "notifications",
                onClick = { navController.navigate("notifications") },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )

            BottomBarItem(
                icon = R.drawable.outline_settings_24,
                label = "Настройки",
                isSelected = currentRoute == "settings",
                onClick = { navController.navigate("settings") },
                selectedColor = selectedColor,
                unselectedColor = unselectedColor
            )
        }
    }
}