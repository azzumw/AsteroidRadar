package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    @Query("select * from asteroids where closeApproachDate>=:todayDate order by closeApproachDate desc")
    suspend fun getAllAsteroids(todayDate:String): List<Asteroid>

    @Query("select * from asteroids where closeApproachDate=:todayDate order by closeApproachDate")
    suspend fun getTodaysAsteroids(todayDate:String):List<Asteroid>

    @Query("select * from asteroids where closeApproachDate =:todayDate and isPotentiallyHazardous=:isHaradous ")
    fun getPotentiallyHazardousFromToday(todayDate: String,isHaradous:Boolean):Flow<List<Asteroid>>

    @Query("select * from asteroids where id=:id")
    fun getAnAsteroid(id:Long):Flow<Asteroid?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAsteroids(asteroids:List<Asteroid>)

    //

    @Query("select * from asteroids where closeApproachDate=:todayDate order by closeApproachDate")
    fun getTodaysAst(todayDate:String):Flow<List<Asteroid>>

    @Query("select * from asteroids where closeApproachDate>=:todayDate order by closeApproachDate desc")
    fun getAll(todayDate:String): Flow<List<Asteroid>>
}