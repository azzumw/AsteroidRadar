package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _asteroidstatus = MutableLiveData<String>()
    val asteroidstatus: LiveData<String> = _asteroidstatus

    private val _photo= MutableLiveData<PictureOfDay>()
    val photo:LiveData<PictureOfDay> = _photo!!

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids get() = _asteroids!!

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val  singleAsteroid get() = _singleAsteroid

    init {
        getApod()
        getAsteroids(AsteroidApiFilter.SHOW_WEEKLY)
    }

    private fun getApod() {

        viewModelScope.launch {
            try {
                _photo.value =  AsteroidApi.retrofitService.getApod(getTodaysDate(),Constants.API_KEY)
                _status.value = photo.value!!.url


                Log.e("MainViewModel:",status.value.toString())
            }catch (e:Exception){
                _status.value = "Failure: ${e.message}"

            }
        }

        Log.e("MainViewModel:",_status.value.toString())
    }

     fun getAsteroids(endD:AsteroidApiFilter){

        val endDate = when(endD){
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }


        viewModelScope.launch {
            try {
                val result = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),
                    endDate,Constants.API_KEY)
                val list = parseAsteroidsJsonResult(JSONObject(result),endD.num)
                _asteroids.value = list
                Log.e("VIEWMODEL-ASTEROIDS:: ",result)
                Log.e("VIEWMODEL-ASTEROIDS:: ",list.size.toString())

            }catch (e:Exception){
                Log.e("getAsteroiuds: " ,e.message.toString())
                _asteroids.value = listOf()
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}