package com.udacity.asteroidradar.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.PictureOfDay
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
    .build()

private val retrofitApodScalar = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

private val retrofitAsteroids = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

interface AsteroidApiService {

    @GET(Constants.APOD_END_POINT)
    suspend fun getApod(
        @Query(Constants.API_KEY_PARAM) apiKey: String
    ): String


    @GET(Constants.ASTEROID_END_POINT)
    suspend fun getNeoWs(
        @Query(Constants.START_DATE_PARAM) startDate: String,
        @Query(Constants.API_KEY_PARAM) apiKey: String
    ): String
}

object AsteroidApi {

    val retrofitServiceScalar: AsteroidApiService by lazy {
        retrofitApodScalar.create(AsteroidApiService::class.java)
    }
    val retrofitService2: AsteroidApiService by lazy {
        retrofitAsteroids.create(AsteroidApiService::class.java)
    }
}

fun parseApod(jsonObject: JSONObject): PictureOfDay? {

    try {
        val title = jsonObject.getString("title")
        val mediatype = jsonObject.getString("media_type")
        val url = jsonObject.getString("url")
        val date = jsonObject.getString("date")
        if (!mediatype.equals("image")) {
            return null
        }
        return if (mediatype.equals("image")) PictureOfDay(
            title = title,
            mediaType = mediatype,
            url = url,
            date = date
        ) else null

    } catch (e: Exception) {
        Log.e("PARSEAPOD method: ", e.message.toString())
        return null
    }
}

fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)
        Log.e("dateAsteroidJsonArray: ", dateAsteroidJsonArray.length().toString())
        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = Asteroid(
                id, codename, formattedDate, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
            )
            asteroidList.add(asteroid)
        }
    }

    Log.e("NetworkUtils", asteroidList.size.toString())
    return asteroidList
}

fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)

    }

    return formattedDateList
}


fun getTodaysDate(): String {
    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat(API_QUERY_DATE_FORMAT) //or use getDateInstance()
    val formatedDate = formatter.format(date)

    return formatedDate.toString()

}

