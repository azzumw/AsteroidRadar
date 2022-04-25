package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
//        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }
            .build()

        val batterLowConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints).build()

//        val deleteRepeatRequest = PeriodicWorkRequestBuilder<DeletePreviousDataWorker>(1, TimeUnit.DAYS)
//            .setConstraints(batterLowConstraints).build()

        val workManager = WorkManager.getInstance()

        workManager.enqueueUniquePeriodicWork(RefreshDataWorker.WORKNAME,ExistingPeriodicWorkPolicy.KEEP,repeatingRequest)

//        workManager.enqueueUniquePeriodicWork(DeletePreviousDataWorker.DELETEWORKNAME,ExistingPeriodicWorkPolicy.KEEP,deleteRepeatRequest)


    }
}