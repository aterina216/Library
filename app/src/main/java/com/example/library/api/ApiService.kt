package com.example.library.api


import com.example.library.data.BookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search.json")
    suspend fun getBooks(
        @Query("q") query: String = "fiction", // фильтруем по ключевому слову "fiction"
        @Query("limit") limit: Int = 1000 // указываем лимит на количество книг
    ): BookResponse
}