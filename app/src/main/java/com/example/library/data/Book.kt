package com.example.library.data


import java.util.ArrayList
import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@SuppressLint("ParcelCreator")
@Entity(tableName = "books")
data class Book(
    @PrimaryKey val key: String, // Уникальный идентификатор
    val title: String,
    val author_name: List<String>,
    val first_publish_year: String?,
    val cover_i: Int?,
    var description: String? = null // Описание, которое может быть пустым
) : Parcelable {

    // Конструктор для чтения из Parcel
    constructor(parcel: Parcel) : this(
        key = parcel.readString() ?: "", // Уникальный ключ книги
        title = parcel.readString() ?: "",
        author_name = parcel.createStringArrayList() ?: emptyList(),
        first_publish_year = parcel.readString(),
        cover_i = parcel.readValue(Int::class.java.classLoader) as? Int,
        description = parcel.readString() // Читаем описание
    )

    // Запись в Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(title)
        parcel.writeStringList(author_name)
        parcel.writeString(first_publish_year)
        parcel.writeValue(cover_i)
        parcel.writeString(description)  // Записываем описание
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}

