package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application, private val asteroidRepository: Repository) : ViewModel() {

    private val allAsteroids: LiveData<List<Asteroid>> = asteroidRepository.asteroids
    private val todayAsteroids: LiveData<List<Asteroid>> = asteroidRepository.todayAsteroids
    private val todayHazardous: LiveData<List<Asteroid>> = asteroidRepository.todayHazardous
    val todayApod: LiveData<PictureOfDay?> = asteroidRepository.todayApod

    val title = Transformations.map(todayApod) {
        //while/when no image is available
        it?.title ?: application.applicationContext.getString(R.string.no_image_title)
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

    private val _singleAsteroid = MutableLiveData<Asteroid>()
    val singleAsteroid get() = _singleAsteroid

    init {
        getApod()
        getAsteroids()
        selectFilter(1)
    }

    fun selectFilter(selectedFilter: Int) {
        filter.value = selectedFilter
    }

    private fun getApod() {
        viewModelScope.launch {
            try {
                asteroidRepository.getApod()
            } catch (e: Exception) {

            }
        }

    }

    private fun getAsteroids() {

        viewModelScope.launch {
            try {
                asteroidRepository.getAsteroids()

            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _singleAsteroid.value = asteroid
    }
}