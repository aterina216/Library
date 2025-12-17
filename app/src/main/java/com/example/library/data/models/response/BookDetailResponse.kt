package com.example.library.data.models.response

import com.example.library.data.models.AuthorForDetail
import com.example.library.data.models.Created
import com.example.library.data.models.LastModified
import com.example.library.data.models.TypeX

data class BookDetailResponse(
    val authors: List<AuthorForDetail>,
    val covers: List<Int>,
    val created: Created,
    val description: String,
    val key: String,
    val last_modified: LastModified,
    val latest_revision: Int,
    val location: String,
    val revision: Int,
    val subject_people: List<String>,
    val subject_places: List<String>,
    val subject_times: List<String>,
    val subjects: List<String>,
    val title: String,
    val type: TypeX
)