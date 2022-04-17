package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Query("select * from asteroids order by closeApproachDate")
    fun getAllAsteroids():List<Asteroid>

    @Query("select * from asteroids where closeApproachDate=:todayDate")
    fun getTodaysAsteroids(todayDate:String):List<Asteroid>

    @Query("select * from asteroids where id=:id")
    fun getAnAsteroid(id:Long):Asteroid

    @Insert
    suspend fun insertAllAsteroids(asteroids:ArrayList<Asteroid>)
}