package com.sudh.weatherforecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sudh.weatherforecast.ForecastDay // Ensure this import is correct
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeeklyForecastAdapter(
    private var dailyForecastList: List<ForecastDay>,
    private val context: Context,
    var apiFetchedTempUnit: String // Changed to 'var' and removed 'private'
) : RecyclerView.Adapter<WeeklyForecastAdapter.DailyForecastViewHolder>() {

    class DailyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayText) // Matches item_forecast.xml
        val iconImageView: ImageView = itemView.findViewById(R.id.icon) // Matches item_forecast.xml
        val tempRangeTextView: TextView = itemView.findViewById(R.id.tempText) // Matches item_forecast.xml
    }

    fun updateData(newDailyForecastList: List<ForecastDay>) {
        dailyForecastList = newDailyForecastList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return DailyForecastViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dailyForecastList.size
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val daily: ForecastDay = dailyForecastList[position]

        val date = Date(daily.dt * 1000)
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        holder.dayTextView.text = dayFormat.format(date).uppercase(Locale.getDefault())

        val minTemp = WeatherConverter.convertTemperature(daily.temp.min, apiFetchedTempUnit)
        val maxTemp = WeatherConverter.convertTemperature(daily.temp.max, apiFetchedTempUnit)
        holder.tempRangeTextView.text = "$minTemp / $maxTemp"

        val iconCode = daily.weather[0].icon
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.iconImageView)
    }
}