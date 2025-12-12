package com.example.library.data.models.response

import com.example.library.data.models.SearchBook

data class OpenLibrarySearchResponse(
    val docs: List<SearchBook>,
    val documentation_url: String,
    val numFound: Int,
    val numFoundExact: Boolean,
    val num_found: Int,
    val offset: Any,
    val q: String,
    val start: Int
)