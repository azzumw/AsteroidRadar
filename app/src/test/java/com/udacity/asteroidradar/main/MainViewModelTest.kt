package com.udacity.asteroidradar.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repository.FakeRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTest{

    private val fakeRepository = FakeRepository()

    @Test
    fun refreshDataFromRepository_showsTodaysAsteroids() {
        //GIVEN: a fresh viewmodel
//        val viewModel = MainViewModel(ApplicationProvider.getApplicationContext(),fakeRepository)
    }
}