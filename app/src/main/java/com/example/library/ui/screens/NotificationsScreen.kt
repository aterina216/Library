package com.example.library.ui.screens

import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.ui.components.DatePickerDialog
import com.example.library.ui.components.ReminderCard
import com.example.library.ui.components.TimePickerDialog
import com.example.library.ui.states.EmptyRemindersState
import com.example.library.ui.viewmodels.BookViewModel
import com.example.library.utils.Reminder
import com.example.library.utils.Reminder.cancelBookReminder
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationsScreen(viewModel: BookViewModel) {

    val books = viewModel.notifications.collectAsState()
    val context = LocalContext.current

    var showEditDateDialog by remember { mutableStateOf(false) }
    var showEditTimeDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var editingReminder by remember { mutableStateOf<BookReminderEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllNotifications()
    }

    if (showEditDateDialog) {
        DatePickerDialog(
            showDialog = true,
            onDismiss = {
                showEditDateDialog = false
                editingReminder = null
            },
            onDateSelected = { date ->
                selectedDate = date
                showEditDateDialog = false
                showEditTimeDialog = true
            }
        )
    }

    if (showEditTimeDialog) {
        TimePickerDialog(
            showDialog = true,
            onDismiss = {
                showEditTimeDialog = false
                selectedDate = null
                editingReminder = null
            },
            onTimeSelected = { time ->
                editingReminder?.let { oldReminder ->
                    selectedDate?.let { date ->
                        val success = Reminder.updateReminder(
                            context = context,
                            oldReminder = oldReminder,
                            newDate = date,
                            newTime = time,
                            viewModel = viewModel
                        )

                        if (success) {
                            Toast.makeText(context, "Напоминание обновлено", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                "Не удалось обновить напоминание",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                showEditTimeDialog = false
                selectedDate = null
                editingReminder = null
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "🔔 Напоминания",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Text(
                        text = "${books.value.size} напоминаний",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            if(books.value.isEmpty()) {
                EmptyRemindersState()
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books.value) {
                        reminder ->
                        ReminderCard(
                            reminder, {
                                viewModel.deleteNotification(
                                    reminder = reminder,
                                    onCancelSystemNotification = {
                                        cancelBookReminder(
                                            context,
                                            reminder
                                        )
                                    }
                                )
                            },
                            onEdit = {
                                    reminderToEdit ->
                                editingReminder = reminderToEdit
                                showEditDateDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}