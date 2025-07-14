package com.sudh.weatherforecast

import android.os.Bundle
import android.content.Intent
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cityInput: EditText
    private lateinit var nextButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)   // XML file you pasted

        // made variable
        cityInput  = findViewById(R.id.cityInput)
        nextButton = findViewById(R.id.nextButton)

        // enter pressed on keyboard
        cityInput.setOnEditorActionListener { _, actionId, event ->
            val isDone = actionId == EditorInfo.IME_ACTION_DONE
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN
            if (isDone || isEnter) {
                goToNextPage()
                true
            } else {
                false
            }
        }

        // arrow button
        nextButton.setOnClickListener { goToNextPage() }
    }

    // checks if the input is empty or not
    private fun goToNextPage() {
        val cityName = cityInput.text.toString().trim()

        if (cityName.isEmpty()) {
            Toast.makeText(this, "Please enter a city first", Toast.LENGTH_SHORT).show()
            return
        }

        // If city is entered, proceed to next screen
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("city_name", cityName)
        startActivity(intent)
        finish()
    }

}