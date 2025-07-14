package com.sudh.weatherforecast

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayout

class SearchBarActivity : AppCompatActivity() {

    private lateinit var searchText: EditText
    private lateinit var recentContainer: FlexboxLayout
    private val recentSearches = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchbar)

        searchText = findViewById(R.id.searchText)
        recentContainer = findViewById(R.id.recentSearchContainer)


        searchText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = searchText.compoundDrawables[2]
                drawableEnd?.let {
                    val drawableWidth = it.bounds.width()
                    val touchX = event.rawX
                    val touchStart = searchText.right - searchText.paddingEnd - drawableWidth

                    if (touchX >= touchStart) {
                        searchText.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Handle Enter/Search key
        searchText.setOnEditorActionListener { _, actionId, event ->
            val input = searchText.text.toString().trim()
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

                if (input.isNotEmpty()) {
                    addToRecent(input)
                    searchText.text.clear()
                    hideKeyboard()
                } else {
                    Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        // Load empty recent tags initially
        updateRecentTags()
    }

    private fun addToRecent(city: String) {
        val formattedCity = city.trim().replaceFirstChar { it.uppercaseChar() }

        // Remove if already exists, then re-add to top
        recentSearches.remove(formattedCity)
        recentSearches.add(0, formattedCity)

        // Keep only latest 5 tags
        if (recentSearches.size > 5) {
            recentSearches.removeAt(recentSearches.size - 1)
        }

        updateRecentTags()
    }

    private fun updateRecentTags() {
        recentContainer.removeAllViews()

        for (city in recentSearches) {
            val tag = TextView(this).apply {
                text = city
                setTextColor(Color.WHITE)
                setPadding(40, 20, 40, 20)
                background = ContextCompat.getDrawable(context, R.drawable.recent_tag_bg)
                layoutParams = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(20, 10, 20, 10)
                }

                setOnClickListener {
                    searchText.setText(city)
                    searchText.setSelection(city.length)
                }
            }
            recentContainer.addView(tag)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchText.windowToken, 0)
    }
}
