package com.example.library.data.models

data class Book(
    val authors: List<Author>,
    val availability: Availability,
    val cover_edition_key: String,
    val cover_id: Int,
    val edition_count: Int,
    val first_publish_year: Int,
    val has_fulltext: Boolean,
    val ia: String,
    val ia_collection: List<String>,
    val key: String,
    val lending_edition: String,
    val lending_identifier: String,
    val printdisabled: Boolean,
    val public_scan: Boolean,
    val subject: List<String>,
    val title: String
)