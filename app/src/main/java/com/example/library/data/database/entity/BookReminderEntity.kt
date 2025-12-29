package com.example.library.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_reminders")
data class BookReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "book_id")
    val bookId: String,

    @ColumnInfo(name = "notification_time")
    val notificationTime: Long,

    @ColumnInfo(name = "is_active")
    var isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "book_title")
    val bookTitle: String,

    @ColumnInfo(name = "book_author")
    val bookAuthor: String?,

    @ColumnInfo(name = "cover_id")
    val coverId: Int?,

    @ColumnInfo(name = "notification_id")
    val notificationId: Int,

    @ColumnInfo(name = "repeat_interval")
    val repeatInterval: Long? = null
)
