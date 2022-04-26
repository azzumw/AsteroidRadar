package com.udacity.asteroidradar.repository

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import kotlinx.coroutines.Dispatchers.IO
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

        withContext(IO) {

            val resultNeows =
                AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(), endDate, Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(resultNeows), endD.num)
            database.asteroidDao().insertAllAsteroids(parsedList)

            val apodResult = AsteroidApi.retrofitServiceScalar.getApod(Constants.API_KEY)
            val parsedApod = parseApod(JSONObject(apodResult))

            parsedApod?.let {
                database.asteroidDao().deleteApods(getTodaysDate())
                database.asteroidDao().insertApod(parsedApod)
            }
        }
    }

    suspend fun getWeeksAsteroids(endD: AsteroidApiFilter) {
        val endDate = when (endD) {
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        withContext(IO) {

            val resultNeows =
                AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(), endDate, Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(resultNeows), endD.num)
            database.asteroidDao().insertAllAsteroids(parsedList)

        }
    }

    suspend fun refreshMarsPhotos() {

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