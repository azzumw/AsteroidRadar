package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database:AppDatabase) {

    private val _asteroidsList = MutableLiveData<List<Asteroid>>()
    val asteroidList:LiveData<List<Asteroid>> get() = _asteroidsList

    suspend fun refreshAsteroids(endD: AsteroidApiFilter) {
        val endDate = when (endD) {
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        withContext(Dispatchers.IO) {
            val result =AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),endDate,Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(result), endD.num)
            database.asteroidDao().insertAllAsteroids(parsedList)
        }
    }

    suspend fun getAllAsteroids() {
        _asteroidsList.postValue(database.asteroidDao().getAllAsteroids(getTodaysDate()))
    }

    suspend fun getTodaysAsteroids() {
        _asteroidsList.postValue(database.asteroidDao().getTodaysAsteroids(getTodaysDate()))
    }

    suspend fun getPotentiallyHazardousFromToday(){
        _asteroidsList.postValue(database.asteroidDao().getPotentiallyHazardousFromToday(getTodaysDate(),true))
    }

}