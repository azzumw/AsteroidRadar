package com.udacity.asteroidradar.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.database.AsteroidDao

class MainViewModelFactory(private val asteroidDao: AsteroidDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(asteroidDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}