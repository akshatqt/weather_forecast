package com.sudh.weatherforecast

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import com.sudh.weatherforecast.WeeklyActivity

class SwipeGestureListener(private val context: Context) : GestureDetector.SimpleOnGestureListener() {

    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null || e2 == null) return false

        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX < 0) {
                    // Left swipe
                    val intent = Intent(context, WeeklyActivity::class.java)
                    context.startActivity(intent)
                }
                return true
            }
        }

        return false
    }

}
