package com.sudh.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class WeeklyActivity : AppCompatActivity() {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

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

        val settings_btn=findViewById<ImageButton>(R.id.navSettings)
        settings_btn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

        }
        val search_btn=findViewById<ImageButton>(R.id.navSearch)
        search_btn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        val home_btn=findViewById<ImageButton>(R.id.navHome)
        home_btn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        }
    }

}
