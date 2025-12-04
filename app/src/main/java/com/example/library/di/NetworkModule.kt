package com.example.library.di

import com.example.library.data.api.ApiBookService
import com.example.library.data.api.BASE_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideBookService(retrofit: Retrofit): ApiBookService {
        return retrofit.create(ApiBookService::class.java)
    }
}

