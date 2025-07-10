package com.sudh.weatherforecast

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pro.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
