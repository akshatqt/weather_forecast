package com.sudh.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudh.weatherforecast.WeeklyForecastAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeeklyActivity : AppCompatActivity() {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeeklyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly)

        // Setup RecyclerView
        recyclerView = findViewById(R.id.weeklyRecyclerView) // Make sure this ID matches your XML
        recyclerView.layoutManager = LinearLayoutManager(this)
        val unitPref = getSharedPreferences("WeatherPreferences", MODE_PRIVATE).getString("unit", "celsius") ?: "celsius"
        val apiUnit = if (unitPref == "celsius") "metric" else "imperial"
        adapter = WeeklyForecastAdapter(emptyList(), this, apiUnit)
        recyclerView.adapter = adapter

        // Load weather data
        fetchForecast()

        // Setup bottom navigation
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchForecast()
    }

    private fun fetchForecast() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val sharedPrefs = getSharedPreferences("WeatherPreferences", MODE_PRIVATE)
                val city = sharedPrefs.getString("city", "Chennai") ?: "Chennai"

                val geoResponse = RetrofitInstance.api.getCoordinatesByCity(
                    cityName = city,
                    apiKey = apiKey
                )

                // Get the body from the geoResponse
                val geoList = geoResponse.body()

                if (!geoList.isNullOrEmpty()) {
                    val lat = geoList[0].lat
                    val lon = geoList[0].lon
                    val unitPref = sharedPrefs.getString("unit", "celsius") ?: "celsius"
                    val unit = if (unitPref == "celsius") "metric" else "imperial"

                    // Use getForecastData from WeatherApiService. The 'exclude' parameter is not used here.
                    val response = RetrofitInstance.api.getForecastData(
                        lat = lat,
                        lon = lon,
                        cnt = 7, // Request 7 days as defined in WeatherApiService.kt
                        units = unit,
                        apiKey = apiKey
                    )

                    // Access the 'list' property from ForecastResponse
                    val dailyForecast = response.body()?.list

                    withContext(Dispatchers.Main) {
                        if (!dailyForecast.isNullOrEmpty()) {
                            val currentUnitPref = sharedPrefs.getString("unit", "celsius") ?: "celsius"
                            val currentApiUnit = if (currentUnitPref == "celsius") "metric" else "imperial"
                            adapter.apiFetchedTempUnit = currentApiUnit // This now works with 'var' in adapter
                            adapter.updateData(dailyForecast.take(7))
                        } else {
                            Toast.makeText(this@WeeklyActivity, "No forecast data available", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@WeeklyActivity, "City not found", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("GeoError", "No coordinates found for city: $city")
                }

            } catch (e: Exception) {
                Log.e("ForecastError", e.message ?: "Unknown error")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WeeklyActivity, "Error fetching forecast", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupNavigation() {
        val settingsBtn = findViewById<ImageButton>(R.id.navSettings)
        val searchBtn = findViewById<ImageButton>(R.id.navSearch)
        val homeBtn = findViewById<ImageButton>(R.id.navHome)


        settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        homeBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


    }
}