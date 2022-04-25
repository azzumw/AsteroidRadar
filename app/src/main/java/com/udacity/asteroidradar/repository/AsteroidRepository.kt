package com.udacity.asteroidradar.repository

import android.util.Log
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
//    val todayApod: LiveData<PictureOfDay?> = database.asteroidDao().getApod().asLiveData()

    private val _size = MutableLiveData<Int>()
    val size :LiveData<Int> get() = _size

    private val _apodResult = MutableLiveData<PictureOfDay?>()
    val apodResult : LiveData<PictureOfDay?> = _apodResult

    suspend fun refreshAsteroids(endD: AsteroidApiFilter) {
        val endDate = when (endD) {
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        withContext(Dispatchers.IO) {
            val resultNeows = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(), endDate, Constants.API_KEY)
            _size.value = resultNeows.length
            val apodResult = AsteroidApi.retrofitService.getApod(Constants.API_KEY)
            val parsedList = parseAsteroidsJsonResult(JSONObject(resultNeows), endD.num)
            _apodResult.value = apodResult
//            val parsedApod = parseApod(JSONObject(apodResult))
            Log.e("Ast Repo: ",parsedList.size.toString())
            database.asteroidDao().insertAllAsteroids(parsedList)
//            parsedApod?.let {
//                database.asteroidDao().insertApod(parsedApod)
//            }
//            if(parsedApod!= null){
//                database.asteroidDao().insertApod(parsedApod)
//            }
        }
    }

    fun resultSize(): Int? {
        return size?.value
    }

}