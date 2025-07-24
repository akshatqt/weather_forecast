package com.sudh.weatherforecast

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var cityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var weatherConditionTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var weatherIconImageView: ImageView

    private lateinit var navSettingsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_root_layout_id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("WeatherPreferences", Context.MODE_PRIVATE)

        cityTextView = findViewById(R.id.city)
        temperatureTextView = findViewById(R.id.temperature)
        weatherConditionTextView = findViewById(R.id.descriptiontxt)
        humidityTextView = findViewById(R.id.humidity)
        windSpeedTextView = findViewById(R.id.windspeed)
        weatherIconImageView = findViewById(R.id.topicon)

        navSettingsButton = findViewById(R.id.navSettings)

        navSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val city = sharedPreferences.getString("city", "Chennai") ?: "Chennai"
        cityTextView.text = city
        fetchWeatherData(city)
    }

    override fun onResume() {
        super.onResume()
        val currentCity = sharedPreferences.getString("city", "Chennai") ?: "Chennai"
        if (cityTextView.text.toString() != currentCity) {
            cityTextView.text = currentCity
        }
        fetchWeatherData(currentCity)
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApiService::class.java)
        val unitPref = sharedPreferences.getString("unit", "celsius") ?: "celsius"
        val apiUnit = if (unitPref == "celsius") "metric" else "imperial"
        val apiKey = BuildConfig.OPENWEATHER_API_KEY

        lifecycleScope.launch {
            try {
                // Corrected: Get the Response and then extract the body
                val geoResponse = api.getCoordinatesByCity(city, 1,apiKey)
                val geoList = geoResponse.body() // Extract the list from the Response body

                if (geoList.isNullOrEmpty()) {
                    Toast.makeText(this@HomeActivity, "City not found", Toast.LENGTH_SHORT).show()
                    resetWeatherUI()
                    return@launch
                }

                val lat = geoList[0].lat
                val lon = geoList[0].lon

                val weatherResponse = api.getCurrentWeather(lat, lon, apiUnit, apiKey)
                val weather = weatherResponse.body()

                if (weather != null) {
                    updateUI(weather, unitPref)
                } else {
                    Toast.makeText(this@HomeActivity, "Weather data not available", Toast.LENGTH_SHORT).show()
                    resetWeatherUI()
                }

            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching weather: ${e.message}")
                Toast.makeText(this@HomeActivity, "Failed to load weather", Toast.LENGTH_SHORT).show()
                resetWeatherUI()
            }
        }
    }

    private fun updateUI(weather: WeatherResponse, unitPref: String) {
        val temp = WeatherConverter.convertTemperature(weather.main.temp, unitPref)
        val windSpeedUnit = if (unitPref == "celsius") "km/h" else "mph"
        val windSpeed = if (unitPref == "celsius") (weather.wind.speed * 3.6) else weather.wind.speed

        temperatureTextView.text = temp
        humidityTextView.text = "Humidity: ${weather.main.humidity}%"
        windSpeedTextView.text = "Wind: ${"%.1f".format(windSpeed)} $windSpeedUnit"
        weatherConditionTextView.text = weather.weather[0].description.replaceFirstChar { it.uppercaseChar() }

        val iconCode = weather.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        Glide.with(this).load(iconUrl).into(weatherIconImageView)
    }

    private fun resetWeatherUI() {
        temperatureTextView.text = "--"
        humidityTextView.text = "Humidity: --%"
        windSpeedTextView.text = "Wind: --"
        weatherConditionTextView.text = "--"
        weatherIconImageView.setImageResource(R.drawable.ic_weather_icon)
    }
}