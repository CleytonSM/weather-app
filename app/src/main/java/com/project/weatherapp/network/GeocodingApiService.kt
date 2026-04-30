package com.project.weatherapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
