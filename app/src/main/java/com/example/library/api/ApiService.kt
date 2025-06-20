package com.example.library.api


import com.example.library.data.BookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search.json")
    fun getBooks(
        @Query("q") query: String // Параметр должен быть строкой
    ): Call<BookResponse>
}