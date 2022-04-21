package com.udacity.asteroidradar

import com.squareup.moshi.Json

data class PictureOfDay(@Json(name = "media_type") val mediaType: String,
                        val title: String = "No Image Downloaded",
                        val url: String)