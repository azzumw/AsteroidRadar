package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Query("select * from asteroids order by closeApproachDate")
    fun getAllAsteroids():LiveData<List<Asteroid>>

    @Query("select * from asteroids where closeApproachDate=:todayDate")
    fun getTodaysAsteroids(todayDate:String):LiveData<List<Asteroid>>

    @Query("select * from asteroids where id=:id")
    fun getAnAsteroid(id:Long):Asteroid

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAsteroids(asteroids:List<Asteroid>)
}