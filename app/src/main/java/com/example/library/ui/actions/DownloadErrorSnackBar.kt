package com.example.library.ui.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material.Snackbar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DownloadErrorSnackbar(
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            // Автоматически скрываем через 5 секунд
            kotlinx.coroutines.delay(5000)
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                action = {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("OK")
                    }
                }
            ) {
                Column {
                    Text(
                        text = "Ошибка загрузки",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}