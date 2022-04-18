package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    @Query("select * from asteroids order by closeApproachDate")
    suspend fun getAllAsteroids(): List<Asteroid>

    @Query("select * from asteroids where closeApproachDate=:todayDate order by closeApproachDate")
    suspend fun getTodaysAsteroids(todayDate:String):List<Asteroid>

    @Query("select * from asteroids where id=:id")
    fun getAnAsteroid(id:Long):Flow<Asteroid?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAsteroids(asteroids:List<Asteroid>)
}