package com.udacity.asteroidradar.repository

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AppDatabase):Repository {

    override val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getWeeklyAsteroids(getTodaysDate()).asLiveData()
    override val todayAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getTodayAsteroids(getTodaysDate()).asLiveData()
    override val todayHazardous: LiveData<List<Asteroid>> =
        database.asteroidDao().getPotentiallyHazardousFromToday(getTodaysDate(), true).asLiveData()
    override val todayApod: LiveData<PictureOfDay?> = database.asteroidDao().getApod().asLiveData()


    override suspend fun getApod() {
        withContext(IO) {
            val apodResult = AsteroidApi.retrofitServiceScalar.getApod(BuildConfig.NASA_API_KEY)
            val parsedApod = parseApod(JSONObject(apodResult))

            parsedApod?.let {
                database.asteroidDao().deleteApods(getTodaysDate())
                database.asteroidDao().insertApod(parsedApod)
            }
        }

    }

    override suspend fun getAsteroids() {

        withContext(IO) {

            val resultNeows =
                AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),BuildConfig.NASA_API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(resultNeows))
            database.asteroidDao().insertAllAsteroids(parsedList)

        }
    }
}