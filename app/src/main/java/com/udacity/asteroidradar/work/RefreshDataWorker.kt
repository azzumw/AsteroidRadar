package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(context:Context,workerParameters: WorkerParameters)
    :CoroutineWorker(context,workerParameters) {
    override suspend fun doWork(): Result {
        val repository = AsteroidRepository(AppDatabase.getDatabase(applicationContext))
        return try {
            repository.refreshAsteroids(AsteroidApiFilter.SHOW_WEEKLY)
            Result.success()
        }catch (e:HttpException){
            Result.retry()
        }
    }
}