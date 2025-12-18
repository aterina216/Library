package com.example.library.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.library.data.models.Author

@Entity(tableName = "books")
data class BookEntity (
    @PrimaryKey
    val id: String,

    val title: String,
    val authors: String,
    val coverId: Int?,
    val firstPublishYear: Int?,
    val subjects: String?,
    val description: String?,

    @ColumnInfo(defaultValue = "")
    val category: String = "",

    @ColumnInfo(name ="shelf_status")
    val shelfStatus: String? = null
)