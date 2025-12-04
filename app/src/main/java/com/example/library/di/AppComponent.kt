package com.example.library.di

import com.example.library.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataBaseModule::class, NetworkModule::class, RepositoryModule::class,
    ViewModelModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
}