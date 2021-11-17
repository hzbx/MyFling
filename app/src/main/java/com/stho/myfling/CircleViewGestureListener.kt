package com.stho.myfling

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import kotlin.math.atan2
import kotlin.math.sqrt

class CircleViewGestureListener(
    private val view: View,
    private val rotate: (Double) -> Unit,
    private val doubleTap: () -> Unit,
) : FlingingGestureDetector.FlingingOnGestureListener {

    private var previousAngle = 0.0
    private var startPositionX: Float = 0f
    private var startPositionY: Float = 0f
    private var startDirectionX: Float = 0f
    private var startDirectionY: Float = 0f
    private var previousValue: Float = 0f

    private val fling = FlingAnimation(view, DynamicAnimation.Z).apply {
        addUpdateListener { _, value, velocity -> onUpdateFling(value, velocity) }
    }

    override fun onDown(e: MotionEvent) {
        Log.d("GESTURE", "onDown() at ${e.x}, ${e.y}")
        fling.cancel()
        previousAngle = getAngle(e.x, e.y)
    }

    override fun onDoubleTap() {
        doubleTap.invoke()
    }

    private fun onRotate(delta: Double) {
        rotate.invoke(delta)
    }

    override fun onScroll(e: MotionEvent) {
        Log.d("GESTURE", "onScroll() at (${e.x}, ${e.y})")
        val alpha = getAngle(e.x, e.y)
        val delta = Degree.normalizeTo180(alpha - previousAngle)
        previousAngle = alpha
        onRotate(delta)
    }

    override fun onFling(e: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.d("GESTURE", "onFling($velocityX, $velocityY) at (${e.x}, ${e.y})")
        fling.apply {
            previousAngle = getAngle(e.x, e.y)
            previousValue = 0f
            val velocity = sqrt(velocityX * velocityX + velocityY * velocityY)
            startPositionX = e.x
            startPositionY = e.y
            startDirectionX = velocityX / velocity
            startDirectionY =  velocityY / velocity
            setStartVelocity(velocity)
            setStartValue(0f)
            friction = 1.1f
            start()
        }
    }

    private fun onUpdateFling(value: Float, velocity: Float) {
        Log.d("FLING", "onUpdateFling($value, $velocity)")
        val distance = value - previousValue
        val x = startPositionX + distance * startDirectionX
        val y = startPositionY + distance * startDirectionY
        val alpha = getAngle(x, y)
        val delta = Degree.normalizeTo180(alpha - previousAngle)
        previousValue = value
        onRotate(delta)
    }

    private fun getAngle(x: Float, y: Float): Double {
        val cx = (view.width / 2).toFloat()
        val cy = (view.height / 2).toFloat()
        return -atan2((y - cy).toDouble(), (x - cx).toDouble()) * 180 / Math.PI + 90
    }

}