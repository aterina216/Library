package com.example.library.data.api

import com.example.library.data.models.response.OpenLibraryResponse
import com.example.library.data.models.response.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiBookService {

    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(
    ): OpenLibraryResponse

    @GET("search.json")
    suspend fun getSearchResult(
        @Query("q") q: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): OpenLibrarySearchResponse

    @GET("subjects/{subject}.json")
    suspend fun getBooksBySubject(
        @Path("subject")subject: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): OpenLibraryResponse
}