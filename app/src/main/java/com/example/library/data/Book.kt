package com.example.library.data


import java.util.ArrayList
import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable


@SuppressLint("ParcelCreator")
data class Book(
    val title: String,
    val author_name: List<String>,  // список авторов
    val first_publish_year: String?,
    val cover_i: Int?  // ID обложки
) : Parcelable {

    constructor(parcel: Parcel) : this(
        title = parcel.readString() ?: "",
        author_name = parcel.createStringArrayList() ?: emptyList(),
        first_publish_year = parcel.readString(),
        cover_i = parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeStringList(author_name)
        parcel.writeString(first_publish_year)
        parcel.writeValue(cover_i)
    }

    // Реализация метода describeContents()
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
