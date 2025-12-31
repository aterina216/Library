package com.example.library

import android.app.Application
import com.example.library.di.AppComponent
import com.example.library.di.DaggerAppComponent
import com.example.library.di.DataBaseModule
import com.example.library.di.RepositoryModule
import com.example.library.di.ViewModelModule

class BooksApp: Application() {

    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .repositoryModule(RepositoryModule())
            .dataBaseModule(DataBaseModule(this))
            .viewModelModule(ViewModelModule())
            .build()
    }
}