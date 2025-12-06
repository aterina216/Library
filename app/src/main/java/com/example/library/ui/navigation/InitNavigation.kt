package com.example.library.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.library.ui.screens.HomeScreen
import com.example.library.ui.viewmodels.BookViewModel

@Composable
fun InitNavigation(viewModel: BookViewModel) {

    val navigationController = rememberNavController()

    NavHost(navController = navigationController, startDestination = "home"){
        composable("home") {
            HomeScreen(viewModel)
        }
    }
}