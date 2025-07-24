package com.sudh.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: AutoCompleteTextView
    private lateinit var nextButton: ImageButton

    private var searchJob: Job? = null
    private var latestCityList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val savedCity = prefs.getString("city_name", null)
        if (!savedCity.isNullOrEmpty()) {
            startHome(savedCity)
            return
        }

        setContentView(R.layout.activity_main)
        cityInput = findViewById(R.id.cityInput)
        nextButton = findViewById(R.id.nextButton)

        cityInput.threshold = 1
        cityInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.length < 2) return

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300) // Debounce user input

                    try {
                        val api = RetrofitGeoClient.geoApi
                        val response = api.getCoordinatesByCity(
                            cityName = query,
                            limit = 5,
                            apiKey = BuildConfig.OPENWEATHER_API_KEY
                        )

                        if (response.isSuccessful && response.body() != null) {
                            val results = response.body()!!
                            latestCityList = results.map {
                                if (it.country.isNotEmpty()) "${it.name}, ${it.country}" else it.name
                            }

                            withContext(Dispatchers.Main) {
                                val adapter = ArrayAdapter(
                                    this@MainActivity,
                                    android.R.layout.simple_dropdown_item_1line,
                                    latestCityList
                                )
                                cityInput.setAdapter(adapter)
                                cityInput.showDropDown()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "No results found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Error fetching suggestions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cityInput.setOnEditorActionListener { _, actionId, event ->
            val isDone = actionId == EditorInfo.IME_ACTION_DONE
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (isDone || isEnter) {
                handleCitySelection()
                true
            } else false
        }

        nextButton.setOnClickListener { handleCitySelection() }
    }

    private fun handleCitySelection() {
        val input = cityInput.text.toString().trim()

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
            return
        }

        if (!latestCityList.contains(input)) {
            Toast.makeText(this, "Please select a valid city from dropdown", Toast.LENGTH_SHORT).show()
            return
        }

        getSharedPreferences("weather_prefs", MODE_PRIVATE).edit()
            .putString("city_name", input)
            .apply()

        startHome(input)
    }

    private fun startHome(cityName: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("city_name", cityName)
        startActivity(intent)
        finish()
    }
}
