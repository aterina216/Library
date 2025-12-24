package com.example.library.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.library.MainActivity
import com.example.library.R

class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent?) {
        val bookTitle = p1?.getStringExtra("BOOK_TITLE") ?: "Книга"
        val bookAuthor = p1?.getStringExtra("BOOK_AUTHOR") ?: "Неизвестный автор"
        val bookId = p1?.getStringExtra("BOOK_ID") ?: ""

        showNotification(p0, bookTitle, bookAuthor, bookId)
    }

    private fun showNotification(
        context: Context,
        title: String,
        author: String,
        bookId: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "book_reminder_channel",
                "Напоминания о книгах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о времени чтения книг"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_BOOK_ID", bookId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            openIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "book_reminder_channel")
            .setSmallIcon(R.drawable.ic_book_notification)
            .setContentTitle("📖 Пора читать!")
            .setContentText("Вы хотели прочитать \"$title\"")
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(bookId.hashCode(), notification)
    }
}