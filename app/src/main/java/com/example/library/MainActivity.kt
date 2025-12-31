package com.example.library

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.library.ui.navigation.InitNavigation
import com.example.library.ui.screens.HomeScreen
import com.example.library.ui.theme.LibraryTheme
import com.example.library.ui.viewmodels.BookViewModel
import com.example.library.ui.viewmodels.ViewModelFactory
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var factory: ViewModelFactory
    val viewModel: BookViewModel by viewModels { factory }

    private var isDarkTheme by mutableStateOf(false)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        (application as BooksApp).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean("dark_theme", false)

        Log.d("ThemeDebug", "Загружена тема: $isDarkTheme")


        setContent {
            LibraryTheme(
                darkTheme = isDarkTheme,
                dynamicColor = true
            ) { InitNavigation(viewModel, ::updateTheme) }
        }
    }

    private fun updateTheme(isDark: Boolean) {
        isDarkTheme = isDark
        getSharedPreferences("app_settings", MODE_PRIVATE).edit().putBoolean("dark_theme", isDark).apply()
        Log.d("ThemeDebug", "Тема изменена на: $isDark")
    }
}

