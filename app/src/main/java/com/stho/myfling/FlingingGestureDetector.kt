package com.stho.myfling

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * FlingingGestureDetector
 *
 * Reacts on motion events for scrolling and flinging.
 *
 * (1) Setup with FlingingGestureDetector(context: Context)
 *
 * (2) Override the methods
 *          onDown(e: MotionEvent)
 *                  to cancel the current fling and prepare starting points of scrolling and flinging
 *          onScroll(e: MotionEvent)
 *                  to rotate
 *          onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float)
 *                  to start the Fling Animation
 *
 * (3) Call onTouchEvent(event: MotionEvent)
 *
 */
class FlingingGestureDetector(context: Context, private val listener: FlingingOnGestureListener) {

    interface FlingingOnGestureListener {
        fun onDown(e: MotionEvent)
        fun onScroll(e: MotionEvent)
        fun onFling(e: MotionEvent, velocityX: Float, velocityY: Float)
        fun onDoubleTap()
    }

    private val gestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            Log.d("GESTURE", "onDown() at ${e.x}, ${e.y}")
            // Do not forward!
            // The gesture detector suppresses events for small movements which would result
            // in strange behavior when starting a movement.
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            listener.onDoubleTap()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            Log.d("GESTURE", "onScroll($distanceX, $distanceY) at (${e2.x}, ${e2.y})")
            // Do not forward!
            // The gesture detector suppresses events for small movements which would result
            // in strange behavior when starting a movement.
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            Log.d("GESTURE", "onFling($velocityX, $velocityY) at (${e2.x}, ${e2.y})")
            listener.onFling(e2, velocityX, velocityY)
            return true
        }
    })

    fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        reactOnTouchEvent(event)
        return true
    }

    private fun reactOnTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("TOUCH", "onDown at (${event.x}, ${event.y})")
                listener.onDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("TOUCH", "onScroll at (${event.x}, ${event.y})")
                listener.onScroll(event)
            }
        }
    }
}

