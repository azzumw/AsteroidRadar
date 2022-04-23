package com.udacity.asteroidradar.repository

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AppDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getAll(getTodaysDate()).asLiveData()
    val todayAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getTodaysAst(getTodaysDate()).asLiveData()
    val todayHazardous: LiveData<List<Asteroid>> =
        database.asteroidDao().getPotentiallyHazardousFromToday(getTodaysDate(), true).asLiveData()
    val todayApod: LiveData<PictureOfDay?> = database.asteroidDao().getApod().asLiveData()

    suspend fun refreshAsteroids(endD: AsteroidApiFilter) {
        val endDate = when (endD) {
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        withContext(Dispatchers.IO) {
            val result =
                AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(), endDate, Constants.API_KEY)
            val apodResult = AsteroidApi.retrofitService.getApod(getTodaysDate(), Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(result), endD.num)
            val parsedApod = parseApod(JSONObject(apodResult))
            database.asteroidDao().insertAllAsteroids(parsedList)
            parsedApod?.let {
                database.asteroidDao().insertApod(parsedApod)
            }
//            if(parsedApod!= null){
//                database.asteroidDao().insertApod(parsedApod)
//            }
        }
    }
}