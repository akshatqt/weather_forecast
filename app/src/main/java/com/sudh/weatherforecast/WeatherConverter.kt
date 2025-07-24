package com.sudh.weatherforecast

import java.text.DecimalFormat

object WeatherConverter {


    private val decimalFormat = DecimalFormat("#.#")


    fun convertTemperature(kelvin: Double, targetUnit: String): String {
        return when (targetUnit) {
            "Celsius" -> {
                val celsius = kelvin - 273.15
                "${celsius.toInt()}°C"
            }
            "Fahrenheit" -> {
                val fahrenheit = (kelvin - 273.15) * 9/5 + 32
                "${fahrenheit.toInt()}°F"
            }
            "Kelvin" -> {
                "${kelvin.toInt()}K"
            }
            else -> "${kelvin.toInt()}K"
        }
    }


    fun convertWindSpeed(speedMps: Double, targetUnit: String): String {
        return when (targetUnit) {
            "m/s (meters/second)" -> "${decimalFormat.format(speedMps)} m/s"
            "km/h (kilometers/hour)" -> {
                val kmh = speedMps * 3.6
                "${decimalFormat.format(kmh)} km/h"
            }
            "mph (miles/hour)" -> {
                val mph = speedMps * 2.23694
                "${decimalFormat.format(mph)} mph"
            }
            else -> "${decimalFormat.format(speedMps)} m/s"
        }
    }


    fun convertPressure(hPa: Double, targetUnit: String): String {
        return when (targetUnit) {
            "hPa (hectopascal)" -> "${hPa.toInt()} hPa"
            "inHg (inches of mercury)" -> {
                val inHg = hPa * 0.02953
                "${decimalFormat.format(inHg)} inHg"
            }
            "mmHg (millimeters of mercury)" -> {
                val mmHg = hPa * 0.750062
                "${decimalFormat.format(mmHg)} mmHg"
            }
            else -> "${hPa.toInt()} hPa"
        }
    }


    fun convertVisibility(meters: Double, targetUnit: String): String {
        return when (targetUnit) {
            "meters" -> "${meters.toInt()} m"
            "kilometers" -> {
                val km = meters / 1000
                "${decimalFormat.format(km)} km"
            }
            "miles" -> {
                val miles = meters / 1609.34
                "${decimalFormat.format(miles)} miles"
            }
            else -> "${meters.toInt()} m"
        }
    }
}