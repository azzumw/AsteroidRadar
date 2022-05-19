package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    companion object {
        const val WORKNAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val repository = AsteroidRepository(AppDatabase.getDatabase(applicationContext))

        return try {
            repository.getAsteroids()

            Result.success()
        } catch (e: HttpException) {
            Log.e("EXCEPTION WORK:", e.message())
            Result.retry()
        }
    }
}