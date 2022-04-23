package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel(application: Application) : ViewModel() {

    private val asteroidRepository = AsteroidRepository(AppDatabase.getDatabase(application))

    private val allAsteroids: LiveData<List<Asteroid>> = asteroidRepository.asteroids
    private val todayAsteroids: LiveData<List<Asteroid>> = asteroidRepository.todayAsteroids
    private val todayHazardous: LiveData<List<Asteroid>> = asteroidRepository.todayHazardous

    private var filter: MutableLiveData<Int> = MutableLiveData()
    val filteredAsteroids: LiveData<List<Asteroid>> = filter.switchMap {
        when (it) {
            1 -> {
                todayAsteroids
            }
            2 -> {
                allAsteroids
            }
            3-> todayHazardous
            else -> {
                allAsteroids
            }
        }
    }

    private val _status = MutableLiveData<String>()
    private val status: LiveData<String> = _status

    private val _photo = MutableLiveData<PictureOfDay>()
    val photo: LiveData<PictureOfDay> = _photo

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val singleAsteroid get() = _singleAsteroid

    val displayTitle = Transformations.map(photo) {
        it.title.ifEmpty {
            "No image"
        }
    }


    init {
        getApod()
        refreshDataFromRepository()
        selectFilter(1)
    }

    fun selectFilter(selectedFilter: Int) {
        filter.value = selectedFilter
    }

    private fun getApod() {

        viewModelScope.launch {
            try {
                _photo.value =
                    AsteroidApi.retrofitService.getApod(getTodaysDate(), Constants.API_KEY)
                _status.value = photo.value!!.url

            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    private fun refreshDataFromRepository(){

        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroids(AsteroidApiFilter.SHOW_WEEKLY)
            }catch (e: Exception) {
                _status.value = e.message
            }
        }
    }


//    fun getAnAsteroid(id:Long):Asteroid = asteroidDao.getAnAsteroid(id)

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}