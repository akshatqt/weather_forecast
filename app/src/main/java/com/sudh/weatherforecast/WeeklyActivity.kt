package com.sudh.weatherforecast

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class WeeklyActivity : AppCompatActivity() {

    private val apiKey = "YOUR_API_KEY_HERE" // store securely in real apps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly) // your layout here

        fetchForecast()
    }

    private fun fetchForecast() {
        lifecycleScope.launch {
            try {
                val city = "London" // eventually from Search or Intent

                val geoResponse = RetrofitInstance.api.getCoordinatesByCity(
                    cityName = city,
                    apiKey = apiKey
                )

                if (geoResponse.isNotEmpty()) {
                    val lat = geoResponse[0].lat
                    val lon = geoResponse[0].lon

                    val forecast = RetrofitInstance.api.get30DayForecast(
                        lat = lat,
                        lon = lon,
                        apiKey = apiKey
                    )

                    Log.d("Forecast", forecast.toString())

                    // TODO: Update RecyclerView with `forecast.list`

                } else {
                    Log.e("GeoError", "No coordinates found for city: $city")
                }
            } catch (e: Exception) {
                Log.e("ForecastError", e.message ?: "Unknown error")
            }
        }
    }

}
