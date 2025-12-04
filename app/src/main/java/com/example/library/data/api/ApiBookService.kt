package com.example.library.data.api

import com.example.library.data.models.response.OpenLibraryResponse
import retrofit2.http.GET

interface ApiBookService {

    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(
    ): OpenLibraryResponse
}