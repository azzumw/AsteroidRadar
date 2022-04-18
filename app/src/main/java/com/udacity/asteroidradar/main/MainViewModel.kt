package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel(private val asteroidDao: AsteroidDao) : ViewModel() {

    var todaysAsteroidsList : LiveData<List<Asteroid>> = asteroidDao.getTodaysAsteroids(getTodaysDate()).asLiveData()

    private val _status = MutableLiveData<String>()
    private val status: LiveData<String> = _status

    private val _photo= MutableLiveData<PictureOfDay>()
    val photo:LiveData<PictureOfDay> = _photo

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val  singleAsteroid get() = _singleAsteroid

    val displayTitle = Transformations.map(photo){
        if(it.title.isEmpty()){
            "No image"
        }else{
            it.title
        }
    }



    init {
        getApod()
        getAsteroidsFromApiAndInsertIntoDB(AsteroidApiFilter.SHOW_WEEKLY)
    }

    private fun getApod() {

        viewModelScope.launch {
            try {
                _photo.value =  AsteroidApi.retrofitService.getApod(getTodaysDate(),Constants.API_KEY)
                _status.value = photo.value!!.url

            }catch (e:Exception){
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    /**Database calls*/

    private fun getAsteroidsFromApiAndInsertIntoDB(endD:AsteroidApiFilter){

        val endDate = when(endD){
            AsteroidApiFilter.SHOW_TODAY -> getNextSevenDaysFormattedDates(endD.num).last()
            else -> getNextSevenDaysFormattedDates(endD.num).last()
        }

        viewModelScope.launch {
            try {
                val result = AsteroidApi.retrofitService2.getNeoWs(getTodaysDate(),
                    endDate,Constants.API_KEY)

                val list = parseAsteroidsJsonResult(JSONObject(result),endD.num)

                asteroidDao.insertAllAsteroids(list)
            }catch (e:Exception){
                _status.value = e.message
            }
        }
    }


    fun getAllAsteroids() {

//        todaysAsteroidsList.value = asteroidDao.getAllAsteroids().asLiveData()
    }

//    fun getTodayAsteroids(date:String): LiveData<List<Asteroid>> = asteroidDao.getTodaysAsteroids(date)

//    fun getAnAsteroid(id:Long):Asteroid = asteroidDao.getAnAsteroid(id)

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}