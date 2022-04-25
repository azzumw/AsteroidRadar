package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AppDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getAll(getTodaysDate()).asLiveData()
    val todayAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao().getTodaysAst(getTodaysDate()).asLiveData()
    val todayHazardous: LiveData<List<Asteroid>> =
        database.asteroidDao().getPotentiallyHazardousFromToday(getTodaysDate(), true).asLiveData()
    val todayApod: LiveData<PictureOfDay?> = database.asteroidDao().getApod(getTodaysDate()).asLiveData()
    private val _status = MutableLiveData<String>()
    val status :LiveData<String> = _status

    suspend fun refreshAsteroids(endD: AsteroidApiFilter) {
        val endDate = when (endD) {
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        withContext(Dispatchers.IO) {

            val resultNeows = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(), endDate, Constants.API_KEY)

            val apodResult = AsteroidApi.retrofitServiceScalar.getApod(Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(resultNeows), endD.num)

            val parsedApod = parseApod(JSONObject(apodResult))

            database.asteroidDao().insertAllAsteroids(parsedList)
//            database.asteroidDao().insertApod(parsedApod!!)

            parsedApod?.let {
                database.asteroidDao().deleteApods(getTodaysDate())
                database.asteroidDao().insertApod(parsedApod)
            }
        }
    }

    suspend fun refreshMarsPhotos(){

//        withContext(Dispatchers.IO){
//            val listResult = AsteroidApi.retrofitServiceScalar.getApod(Constants.API_KEY)
//            val parsedApod = parseApod(JSONObject(listResult))
//            parsedApod?.let {
//                database.asteroidDao().insertApod(parsedApod)
//            }
//        }

//        coroutineScope {
//            val listResult = AsteroidApi.retrofitServiceScalar.getApod(Constants.API_KEY)
//            _status.value = listResult
//
//        }
    }

}