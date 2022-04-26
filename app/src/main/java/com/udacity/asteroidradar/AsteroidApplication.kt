package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.work.*
//import androidx.work.*
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.work.RefreshDataWorker
//import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    val workRequest: OneTimeWorkRequest by lazy {
        val constraints = getConstraints()
        OneTimeWorkRequestBuilder<RefreshDataWorker>()
            .setConstraints(constraints)
            .addTag("TAG").build()
    }

    val periodicWorkRequest: PeriodicWorkRequest by lazy {
        /**If you want to test intervals please update param repeatInterval, and TimeUnit**/
        PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(getConstraints())
//            .setBackoffCriteria(
//                BackoffPolicy.LINEAR,
//                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
//                TimeUnit.MILLISECONDS
//            )
            .build()
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
//            setupRecurringWork()
            refreshRequest()
        }
    }

    fun refreshRequest() {
//        WorkManager.getInstance().enqueue(periodicWorkRequest)
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORKNAME,
            ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest
        )
    }

    private fun getConstraints(): Constraints {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            //            .apply {
            //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //                    setRequiresDeviceIdle(true)
            //                }
            //            }
            .build()
        return constraints
    }

    private fun setupRecurringWork() {

//        val constraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiresBatteryNotLow(true)
//            .setRequiredNetworkType(NetworkType.UNMETERED)
//            .apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    setRequiresDeviceIdle(true)
//                }
//            }
//            .build()

//        val batterLowConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()


//        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
//            .setConstraints(constraints).build()

//        val deleteRepeatRequest = PeriodicWorkRequestBuilder<DeletePreviousDataWorker>(1, TimeUnit.DAYS)
//            .setConstraints(batterLowConstraints).build()

//        WorkManager.getInstance().enqueueUniquePeriodicWork(RefreshDataWorker.WORKNAME,
//            ExistingPeriodicWorkPolicy.KEEP,repeatingRequest)

//        workManager.enqueueUniquePeriodicWork(DeletePreviousDataWorker.DELETEWORKNAME,ExistingPeriodicWorkPolicy.KEEP,deleteRepeatRequest)
    }

}