package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    @Query("select * from apod")
    fun getApod(): Flow<PictureOfDay>

    @Insert(entity = PictureOfDay::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertApod(apod: PictureOfDay)


    @Query("select * from asteroids where closeApproachDate =:todayDate and isPotentiallyHazardous=:isHaradous ")
    fun getPotentiallyHazardousFromToday(
        todayDate: String,
        isHaradous: Boolean
    ): Flow<List<Asteroid>>

    @Query("select * from asteroids where id=:id")
    fun getAnAsteroid(id: Long): Flow<Asteroid?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAsteroids(asteroids: List<Asteroid>)

    @Query("select * from asteroids where closeApproachDate=:todayDate order by closeApproachDate")
    fun getTodaysAst(todayDate: String): Flow<List<Asteroid>>

    @Query("select * from asteroids where closeApproachDate>=:todayDate order by closeApproachDate desc")
    fun getAll(todayDate: String): Flow<List<Asteroid>>

//    @Query("delete from asteroids where closeApproachDate <:todayDate")
//    fun deletePreviousAsteroids(todayDate: String)
}