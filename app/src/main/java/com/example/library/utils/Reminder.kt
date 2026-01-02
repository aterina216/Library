package com.example.library.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.library.data.database.entity.BookReminderEntity
import com.example.library.data.models.Book
import com.example.library.data.models.response.BookDetailResponse
import com.example.library.services.ReminderReceiver
import com.example.library.ui.viewmodels.BookViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object Reminder {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleBookReminder(
        context: Context,
        date: LocalDate,
        time: LocalTime,
        book: BookDetailResponse,
        viewModel: BookViewModel? = null
    ): Boolean {
        try {
            Log.d("Reminder", "🔔 Начинаем установку напоминания для книги: ${book.title}")
            val dateTime = LocalDateTime.of(date, time)
            val triggerAtMillis = dateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            Log.d("Reminder", "📅 Выбрана дата: $dateTime")
            Log.d("Reminder", "⏰ Timestamp: $triggerAtMillis")
            Log.d("Reminder", "🕐 Текущее время: ${System.currentTimeMillis()}")

            if(triggerAtMillis < System.currentTimeMillis()){
                Log.w("Reminder", "Попытка установить напоминание в прошлом")
                Toast.makeText(context, "Нельзя установить напоминание в прошлом",
                    Toast.LENGTH_SHORT).show()
                return false
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(!PermissionHelper.hasAlarmPermission(context)) {
                    Log.w("Reminder", "Нет разрешения на точные будильники")

                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                    return false
                }
            }

            val bookId = book.key.substringAfterLast("/")
            Log.d("Reminder", "📖 ID книги: $bookId")

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("BOOK_ID", bookId)
                putExtra("BOOK_TITLE", book.title)
            }

            val reminderId = (bookId.hashCode() + dateTime.hashCode()).and(0x7FFFFFFF)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

            if (!notificationsEnabled) {
                Log.d("Notifications", "Уведомления отключены в настройках")
                return false
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.d("Reminder", "✅ Напоминание установлено (setExactAndAllowWhileIdle)")
            }
            else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.d("Reminder", "✅ Напоминание установлено (setExact)")
            }

            val reminderEntity = BookReminderEntity(
                bookId = book.key.substringAfterLast("/"),
                notificationTime = triggerAtMillis,
                bookTitle = book.title ?: "Без названия",
                bookAuthor = book.authors.joinToString(", ") {
                    it.author.key.substringAfterLast("/")
                },
                coverId = book.covers.firstOrNull(),
                notificationId = reminderId,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )
            Log.d("Reminder", "📝 Создан BookReminderEntity.kt: ${reminderEntity.bookTitle}")


            if (viewModel != null) {
                Log.d("Reminder", "🔄 Передаем напоминание во ViewModel")
                viewModel.addBookFromNotification(reminderEntity)
            } else {
                Log.e("Reminder", "❌ ViewModel не передан!")
                return false
            }
            return true
        }
        catch (e: SecurityException) {
            Log.e("Reminder", "❌ Ошибка безопасности: ${e.message}", e)
            Toast.makeText(context, "Нет разрешения на установку будильника", Toast.LENGTH_SHORT).show()
            return false
        }
        catch (e: Exception) {
            Log.e("Reminder", "❌ Ошибка установки напоминания: ${e.message}", e)
            Toast.makeText(context, "Ошибка установки напоминания", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun cancelBookReminder(
        context: Context,
        reminderEntity: BookReminderEntity
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderEntity.notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("Reminder", "✅ Системное напоминание отменено: ${reminderEntity.notificationId}")
        }
        catch (e: Exception) {
            Log.e("Reminder", "❌ Ошибка отмены напоминания: ${e.message}", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateReminder(
        context: Context,
        oldReminder: BookReminderEntity,
        newDate: LocalDate,
        newTime: LocalTime,
        viewModel: BookViewModel
    ): Boolean {
        try {
            Log.d("Reminder", "🔄 Обновляем напоминание: ${oldReminder.id}")

            cancelBookReminder(context, oldReminder)

            val newDateTime = LocalDateTime.of(newDate, newTime)
            val triggerAtMillis = newDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if(triggerAtMillis < System.currentTimeMillis()) {
                Toast.makeText(context, "Нельзя установить напоминание в прошлом", Toast.LENGTH_SHORT).show()
                return false
            }
            val newNotificationId = (oldReminder.bookId.hashCode() + newDateTime.hashCode()).and(0x7FFFFFFF)
            viewModel.updateReminder(
                oldReminder,
                triggerAtMillis,
                newNotificationId
            )

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("BOOK_ID", oldReminder.bookId)
                putExtra("BOOK_TITLE", oldReminder.bookTitle)
                putExtra("BOOK_AUTHOR", oldReminder.bookAuthor ?: "")
                putExtra("NOTIFICATION_ID", newNotificationId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                newNotificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }

            Log.d("Reminder", "✅ Напоминание обновлено на: $newDateTime")
            return true
        }
        catch (e: Exception) {
            Log.e("Reminder", "❌ Ошибка обновления напоминания", e)
            Toast.makeText(context, "Ошибка обновления напоминания", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}