package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.Asteroid


@Database(entities = [Asteroid::class], version = 1)
abstract class AppDatabase:RoomDatabase() {

    abstract fun asteroidDao():AsteroidDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "asteroid_database")
                    .build()
                INSTANCE = instance

                instance
            }
        }

    }
}