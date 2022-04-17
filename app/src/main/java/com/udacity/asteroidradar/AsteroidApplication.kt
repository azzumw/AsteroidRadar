package com.udacity.asteroidradar

import android.app.Application
import com.udacity.asteroidradar.database.AppDatabase

class AsteroidApplication:Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}