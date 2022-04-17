package com.udacity.asteroidradar.main

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel(private val asteroidDao: AsteroidDao) : ViewModel() {

    val allasteroids: List<Asteroid> = asteroidDao.getAllAsteroids()

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _asteroidstatus = MutableLiveData<String>()
    val asteroidstatus: LiveData<String> = _asteroidstatus

    private val _photo= MutableLiveData<PictureOfDay>()
    val photo:LiveData<PictureOfDay> = _photo!!

    val displayTitle = Transformations.map(photo){
        if(it.title.isNullOrEmpty()){
            "No image"
        }else{
            it.title
        }
    }

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids get() = _asteroids!!

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val  singleAsteroid get() = _singleAsteroid

    init {
        getApod()
//        getAsteroids(AsteroidApiFilter.SHOW_TODAY)
        insertAsteroidsInDatabase(AsteroidApiFilter.SHOW_TODAY)

        Log.e("VIEWMODEL DATABASE:",allasteroids.size.toString())

    }

    private fun getApod() {

        viewModelScope.launch {
            try {
                _photo.value =  AsteroidApi.retrofitService.getApod(getTodaysDate(),Constants.API_KEY)
                _status.value = photo.value!!.url

                Log.e("VM APOD status:",status.value.toString())
            }catch (e:Exception){
                _status.value = "Failure: ${e.message}"

            }
        }
    }

//     fun getAsteroids(endD:AsteroidApiFilter){
//
//        val endDate = when(endD){
//            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
//            else -> getNextSevenDaysFormattedDates(endD.num).last()
//        }
//
//
//        viewModelScope.launch {
//            try {
//                val result = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),
//                    endDate,Constants.API_KEY)
//                val list = parseAsteroidsJsonResult(JSONObject(result),endD.num)
//                _asteroids.value = list
//                Log.e("VIEWMODEL-ASTEROIDS:: ",result)
//                Log.e("VIEWMODEL-ASTEROIDS:: ",list.size.toString())
//
//            }catch (e:Exception){
//                Log.e("getAsteroiuds: " ,e.message.toString())
//                _asteroids.value = listOf()
//            }
//        }
//    }

    /**Database calls*/

    private fun insertAsteroidsInDatabase(endD:AsteroidApiFilter){

        val endDate = when(endD){
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        viewModelScope.launch {
            val result = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),
                endDate,Constants.API_KEY)

            val list = parseAsteroidsJsonResult(JSONObject(result),endD.num)

            asteroidDao.insertAllAsteroids(list)
        }
    }


//    fun getAllAsteroids(): LiveData<List<Asteroid>> = asteroidDao.getAllAsteroids()

    fun getTodayAsteroids(date:String): LiveData<List<Asteroid>> = asteroidDao.getTodaysAsteroids(date)

    fun getAnAsteroid(id:Long):Asteroid = asteroidDao.getAnAsteroid(id)

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}