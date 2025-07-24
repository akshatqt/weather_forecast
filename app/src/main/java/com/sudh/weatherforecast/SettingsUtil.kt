package com.sudh.weatherforecast

import android.content.Context
import android.content.SharedPreferences


object SettingsUtil {

    private const val PREFS_NAME = "WeatherAppPrefs"

    private const val KEY_TEMP_UNIT_INDEX = "temp_unit"
    private const val KEY_WIND_UNIT_INDEX = "wind_unit"
    private const val KEY_AIR_PRESSURE_UNIT_INDEX = "air_pressure_unit"
    private const val KEY_VISIBILITY_UNIT_INDEX = "visibility_unit"

    private const val KEY_SEVERE_WARNINGS_ENABLED = "severe_warnings_enabled"
    private const val KEY_RAIN_ALERTS_ENABLED = "rain_alerts_enabled"
    private const val KEY_DAILY_FORECAST_ENABLED = "daily_forecast_enabled"

    private lateinit var prefs: SharedPreferences


    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }


    fun getTempUnitSelectedIndex(): Int {
        return prefs.getInt(KEY_TEMP_UNIT_INDEX, 0) // Default to index 0 (Celsius)
    }

    /**
     * Returns the unit string suitable for OpenWeatherMap API's 'units' parameter.
     */
    fun getApiTempUnit(): String {
        return when (getTempUnitSelectedIndex()) {
            0 -> "metric" // Celsius
            1 -> "imperial" // Fahrenheit
            2 -> "standard" // Kelvin
            else -> "metric" // Default to metric
        }
    }


    fun getDisplayTempUnitString(context: Context): String {
        val index = getTempUnitSelectedIndex()
        return context.resources.getStringArray(R.array.temperature_units)[index].split(" ")[0]
    }



    fun getWindUnitSelectedIndex(): Int {
        return prefs.getInt(KEY_WIND_UNIT_INDEX, 0) // Default to index 0 (m/s)
    }


    fun getDisplayWindUnitString(context: Context): String {
        val index = getWindUnitSelectedIndex()
        return context.resources.getStringArray(R.array.wind_units)[index]
    }



    fun getAirPressureUnitSelectedIndex(): Int {
        return prefs.getInt(KEY_AIR_PRESSURE_UNIT_INDEX, 0)
    }


    fun getDisplayPressureUnitString(context: Context): String {
        val index = getAirPressureUnitSelectedIndex()
        return context.resources.getStringArray(R.array.air_pressure_units)[index]
    }



    fun getVisibilityUnitSelectedIndex(): Int {
        return prefs.getInt(KEY_VISIBILITY_UNIT_INDEX, 0) // Default to index 0 (meters)
    }

    /**
     * Returns the human-readable string for display, e.g., "meters", "kilometers", "miles".
     */
    fun getDisplayVisibilityUnitString(context: Context): String {
        val index = getVisibilityUnitSelectedIndex()
        return context.resources.getStringArray(R.array.visibility_units)[index]
    }


    // --- Notification Preference Getters ---

    fun isSevereWarningsEnabled(): Boolean {
        return prefs.getBoolean(KEY_SEVERE_WARNINGS_ENABLED, false) // Default to false
    }

    fun isRainAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_RAIN_ALERTS_ENABLED, false) // Default to false
    }

    fun isDailyForecastEnabled(): Boolean {
        return prefs.getBoolean(KEY_DAILY_FORECAST_ENABLED, false) // Default to false
    }
}