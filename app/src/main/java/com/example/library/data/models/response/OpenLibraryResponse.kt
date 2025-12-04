package com.example.library.data.models.response

import com.example.library.data.models.Book

data class OpenLibraryResponse(
    val key: String,
    val name: String,
    val solr_query: String,
    val subject_type: String,
    val work_count: Int,
    val works: List<Book>
)