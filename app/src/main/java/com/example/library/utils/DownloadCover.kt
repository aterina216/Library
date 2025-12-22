package com.example.library.utils

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.library.ui.states.DownloadState

object DownloadCover {

    @Composable
    fun DownloadCoverWithPermissions(
        context: Context,
        bookTitle: String,
        coverId: Int,
        downloadState: DownloadState,
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val download = {
            startDownload(context, bookTitle, coverId, downloadState)
        }

        if (PermissionHelper.hasStoragePermission(context)) {
            LaunchedEffect(Unit) { download() }
        } else {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(PermissionHelper.getRequiredPermissions())
            }
        }
    }

    fun startDownload(
        context: Context,
        bookTitle: String,
        coverId: Int,
        downloadState: DownloadState
    ) {
        downloadState.isDownLoading = true
        downloadState.downLoadError = null
        downloadState.downLoadSuccess = false

        CoverDownloader.downloadBookCover(
            context,
            bookTitle,
            coverId,
            onSuccess = { filePath ->
                downloadState.isDownLoading = false
                downloadState.downLoadSuccess = true
                Toast.makeText(context, "Обложка скачана!", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                downloadState.isDownLoading = false
                downloadState.downLoadError = error
            }
        )
    }
}