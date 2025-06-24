package com.example.library.data

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import java.util.ArrayList

data class Book(
    val title: String,
    val author_name: List<String>,  // список авторов
    val first_publish_year: String?,
    val cover_i: Int?  // ID обложки
)

