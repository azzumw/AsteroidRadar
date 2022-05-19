package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

interface Repository {

    val asteroids:LiveData<List<Asteroid>>
    val todayAsteroids:LiveData<List<Asteroid>>
    val todayHazardous:LiveData<List<Asteroid>>
    val todayApod:LiveData<PictureOfDay?>

    suspend fun getApod()

    suspend fun getAsteroids()
}
