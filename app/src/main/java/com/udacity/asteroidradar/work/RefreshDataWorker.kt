package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.calculate
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    companion object {
        const val WORKNAME = "RefreshDataWorker"
        const val DATA_KEY = "num"
    }

    override suspend fun doWork(): Result {
        val repository = AsteroidRepository(AppDatabase.getDatabase(applicationContext))

        return try {
            repository.getAsteroids()

            val x = calculate(2, 5)
            val output = Data.Builder().putInt(DATA_KEY, x).build()
            Result.success(output)
        } catch (e: HttpException) {
            Log.e("EXCEPTION WORK:", e.message())
            Result.retry()
        }
    }
}