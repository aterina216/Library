package com.example.library.data.models

data class SearchBook(
    val author_key: List<String>? = null,
    val author_name: List<String>? = null,  // ← НУЖЕН NULLABLE!
    val cover_edition_key: String? = null,
    val cover_i: Int? = null,               // ← НУЖЕН NULLABLE!
    val ebook_access: String? = null,
    val edition_count: Int? = null,
    val first_publish_year: Int? = null,    // ← НУЖЕН NULLABLE!
    val has_fulltext: Boolean? = null,
    val ia: List<String>? = null,
    val ia_collection_s: String? = null,
    val key: String? = null,                // ← НУЖЕН NULLABLE!
    val language: List<String>? = null,
    val lending_edition_s: String? = null,
    val lending_identifier_s: String? = null,
    val public_scan_b: Boolean? = null,
    val title: String? = null               // ← НУЖЕН NULLABLE!
)