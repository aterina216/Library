package com.example.library.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.library.ui.components.BottomBar
import com.example.library.ui.screens.BookDetailScreen
import com.example.library.ui.screens.HistoryScreen
import com.example.library.ui.screens.HomeScreen
import com.example.library.ui.screens.MyBookShelfScreen
import com.example.library.ui.viewmodels.BookViewModel

@Composable
fun InitNavigation(viewModel: BookViewModel) {

    val navigationController = rememberNavController()
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Проверяем, начинается ли текущий маршрут с "book_detail/"
    val showBottomBar = when {
        currentRoute?.startsWith("book_detail/") == true -> false
        else -> true
    }
    Scaffold(bottomBar = {

        if(showBottomBar) {
            BottomBar(navigationController)
        }
    })
    {
        PaddingValues->

        NavHost(navController = navigationController, startDestination = "home",
            modifier = Modifier.padding(PaddingValues(bottom = 0.dp))) {
            composable("home") {
                HomeScreen(viewModel, navigationController)
            }
            composable("history") {
                HistoryScreen(viewModel, navigationController)
            }
            composable("book_shelf") {
                MyBookShelfScreen(viewModel, navigationController)
            }

            composable("book_detail/{bookId}",
                arguments = listOf(navArgument("bookId") {type = NavType.StringType}
                )) { backStackEntry ->
                BookDetailScreen(
                    bookId = backStackEntry.arguments?.getString("bookId") ?: "",
                    navigationController,
                    viewModel
                )
            }
        }
    }
}