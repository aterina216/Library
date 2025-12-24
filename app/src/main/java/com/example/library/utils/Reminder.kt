package com.example.library.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.library.data.models.Book
import com.example.library.data.models.response.BookDetailResponse
import com.example.library.services.ReminderReceiver
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
        book: BookDetailResponse
    ): Boolean {
        try {
            Log.d("Reminder", "🔔 Начинаем установку напоминания для книги: ${book.title}")
            val dateTime = LocalDateTime.of(date, time)
            val triggerAtMillis = dateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

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
}