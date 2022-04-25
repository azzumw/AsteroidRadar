package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : ViewModel() {

    private val asteroidRepository = AsteroidRepository(AppDatabase.getDatabase(application))

    private val allAsteroids: LiveData<List<Asteroid>> = asteroidRepository.asteroids
    private val todayAsteroids: LiveData<List<Asteroid>> = asteroidRepository.todayAsteroids
    private val todayHazardous: LiveData<List<Asteroid>> = asteroidRepository.todayHazardous
    val todayApod: LiveData<PictureOfDay?> = asteroidRepository.todayApod

    val title  = Transformations.map(todayApod){
        it?.title ?: "White cosmos"
    }


    private var filter: MutableLiveData<Int> = MutableLiveData()
    val filteredAsteroids: LiveData<List<Asteroid>> = filter.switchMap {
        when (it) {
            1 -> {
                todayAsteroids
            }
            2 -> {
                allAsteroids
            }
            3 -> todayHazardous
            else -> {
                allAsteroids
            }
        }
    }

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status


    private val _photo = MutableLiveData<PictureOfDay>()
    val photo: LiveData<PictureOfDay> = _photo

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val singleAsteroid get() = _singleAsteroid



    init {
//        getMarsPhotos()
        refreshDataFromRepository()
        selectFilter(1)
    }

    fun selectFilter(selectedFilter: Int) {
        filter.value = selectedFilter
    }

    private fun refreshDataFromRepository() {

        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroids(AsteroidApiFilter.SHOW_WEEKLY)

            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    private fun getMarsPhotos(){
        viewModelScope.launch {
            try {
//                val result = AsteroidApi.retrofitServiceScalar.getApod(Constants.API_KEY)
//                _status.value = result
                asteroidRepository.refreshMarsPhotos()
                _status.value = asteroidRepository.status.value
//                _status.value = asteroidRepository.status.value
            } catch (e: Exception) {
                _status.value = "Faillure: ${e.message}"
            }
        }

    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}