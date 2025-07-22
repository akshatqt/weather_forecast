package com.sudh.weatherforecast

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",       // Celsius
        @Query("appid") apiKey: String
    ): WeatherResponse

    @GET("forecast/climate")
    suspend fun getWeeklyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") cnt: Int = 7, // limit to 7 days
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeeklyActivity

    @GET("data/2.5/forecast/climate")
    suspend fun get30DayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("cnt") cnt: Int = 7, // Get 7-day forecast
        @Query("units") units: String = "metric" // Celsius
    ): ForecastResponse

    @GET("geo/1.0/direct")
    suspend fun getCoordinatesByCity(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeoCodingResponseItem>


}
