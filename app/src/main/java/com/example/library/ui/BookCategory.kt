package com.example.library.ui

enum class BookCategory(val displayName: String,
    val subject: String) {

    FICTION("Художественные", "fiction"),
    SCIENCE_FICTION("Фантастика", "science_fiction"),
    DETECTIVE("Детективы", "detective_and_mystery_stories"),
    ROMANCE("Романы", "romance"),
    CLASSICS("Классика", "classics")
}