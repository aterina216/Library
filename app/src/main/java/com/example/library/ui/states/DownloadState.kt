package com.example.library.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class DownloadState {

    var isDownLoading by mutableStateOf(false)
    var downLoadError by mutableStateOf<String?>(null)
    var downLoadSuccess by mutableStateOf(false)
}