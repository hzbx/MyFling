package com.stho.myfling

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation

class FlingViewGestureListener(
    private val view: View,
    private val rotate: (Double) -> Unit,
    private val doubleTap: () -> Unit,
) : FlingingGestureDetector.FlingingOnGestureListener {

    private var previousAngle: Double = 0.0
    private var startPosition: Float = 0f
    private var previousValue: Float = 0f

    private val fling = FlingAnimation(view, DynamicAnimation.Z).apply {
        addUpdateListener { _, value, velocity -> onUpdateFling(value, velocity) }
    }

    override fun onDown(e: MotionEvent) {
        Log.d("GESTURE", "onDown() at ${e.x}, ${e.y}")
        fling.cancel()
        previousAngle = getAngle(e.y)
    }

    override fun onDoubleTap() {
        doubleTap.invoke()
    }

    private fun onRotate(delta: Double) {
        rotate.invoke(delta)
    }

    override fun onScroll(e: MotionEvent) {
        Log.d("GESTURE", "onScroll() at (${e.x}, ${e.y})")
        val alpha = getAngle(e.y)
        val delta = Degree.normalizeTo180(alpha - previousAngle)
        previousAngle = alpha
        onRotate(delta)
    }

    override fun onFling(e: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.d("GESTURE", "onFling($velocityX, $velocityY) at (${e.x}, ${e.y})")
        fling.apply {
            previousAngle = getAngle(e.y)
            startPosition = e.y
            previousValue = 0f
            setStartVelocity(velocityY)
            setStartValue(0f)
            friction = 1.1f
            start()
        }
    }

    private fun onUpdateFling(value: Float, velocity: Float) {
        Log.d("FLING", "onUpdateFling($value, $velocity)")
        val distance = value - previousValue
        val alpha = getAngle(startPosition + distance)
        val delta = Degree.normalizeTo180(alpha - previousAngle)
        previousValue = value
        onRotate(delta)
    }

    private fun getAngle(y: Float): Double {
        val l = view.height / 2
        val d = l - y
        val r = d / l
        return -Degree.arcCos(r.coerceIn(-1f, +1f).toDouble())
    }
}
