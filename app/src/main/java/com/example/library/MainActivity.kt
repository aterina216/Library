package com.example.library

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    override fun onCreate(savedInstanceState: Bundle?) {

        (application as BooksApp).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            LibraryTheme { InitNavigation(viewModel) }

        }
    }
}

