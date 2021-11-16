package com.stho.myfling

import android.graphics.Bitmap
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.VelocityTracker
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CircleView : View {

    interface OnRotateListener {
        fun onRotate(delta: Double)
    }

    interface OnDoubleTapListener {
        fun onDoubleTap()
    }

    private var transformation: Matrix? = null
    private var rotateListener: OnRotateListener? = null
    private var doubleTapListener: OnDoubleTapListener? = null
    private var previousAngle = 0.0
    private var startPositionX: Float = 0f
    private var startPositionY: Float = 0f
    private var startDirectionX: Float = 0f
    private var startDirectionY: Float = 0f
    private var previousValue: Float = 0f
    private var gestureDetector: GestureDetector? = null
    private var velocityTracker: VelocityTracker? = null
    private var angle = 13.0
    private var ring: Bitmap? = null

    private val fling = FlingAnimation(this, DynamicAnimation.Z).apply {
        addUpdateListener { animation, value, velocity -> onUpdateFling(value, velocity) }
    }

    constructor(context: Context?) : super(context) {
        setupGestureDetector()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupGestureDetector()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setupGestureDetector()
    }

    private fun setup() {
        setupGestureDetector()
        setupVelocityTracker()
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                this@CircleView.onDown(e)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                this@CircleView.onDoubleTap()
                return super.onDoubleTap(e)
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                this@CircleView.onFling(e1, e2, velocityX, velocityY)
                return true
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                this@CircleView.onScroll(e1, e2, distanceX, distanceY)
                return true
            }
        })
    }

    private fun setupVelocityTracker() {
        velocityTracker = VelocityTracker.obtain()
    }

    private fun onRotate(delta: Double) {
        if (rotateListener != null) rotateListener!!.onRotate(delta)
    }

    private fun onDoubleTap() {
        if (doubleTapListener != null) doubleTapListener!!.onDoubleTap()
    }

    private fun onDown(e: MotionEvent) {
        fling.cancel()
        previousAngle = getAngle(e.x, e.y)
    }

    private fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        Log.d("GESTURE", "onScroll($distanceX, $distanceY)")
        val alpha = getAngle(e2.x, e2.y)
        val delta = ensureAngleInRange(alpha - previousAngle)
        previousAngle = alpha
        onRotate(delta)
    }

    private fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float) {
        Log.d("GESTURE", "onFling($velocityX, $velocityY)")
        fling.apply {
            previousAngle = getAngle(e2.x, e2.y)
            startPositionX = e2.x
            startPositionY = e2.y
            val dx = e2.x - e1.x
            val dy = e2.y - e1.y
            val norm = sqrt(dx * dx + dy * dy)
            startDirectionX = dx / norm
            startDirectionY = dy / norm
            val velocity = sqrt(velocityX * velocityX + velocityY * velocityY)
            previousValue = 0f
            setStartVelocity(velocity)
            setStartValue(0f)
            friction = 1.1f
            start()
        }
    }

    private fun onUpdateFling(value: Float, velocity: Float) {
        Log.d("GESTURE", "onUpdateFling($value, $velocity)")
        val distance = value - previousValue
        val x = startPositionX + distance * startDirectionX
        val y = startPositionY + distance * startDirectionY
        val alpha = getAngle(x, y)
        val delta = ensureAngleInRange(alpha - previousAngle)
        previousValue = value
        onRotate(delta)
    }

    fun setOnRotateListener(listener: OnRotateListener?) {
        rotateListener = listener
    }

    fun setOnDoubleTapListener(listener: OnDoubleTapListener?) {
        doubleTapListener = listener
    }

    internal fun setRotationAngle(newAngle: Double) {
        if (angle != newAngle) {
            angle = newAngle
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(event)
        velocityTracker?.addMovement(event)
        return true
    }

    private fun ensureMatrix(): Matrix
        = transformation ?: onCreate(context)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val matrix = ensureMatrix()

        val imageHeight = ring!!.height
        val px = (width / 2).toFloat()
        val py = (height / 2).toFloat()
        val dx = (px - (imageHeight / 2)).roundToInt().toFloat()
        val dy = (py - (imageHeight / 2)).roundToInt().toFloat()

        // ring + degrees + numbers
        matrix.setTranslate(dx, dy)
        matrix.postRotate(-angle.toFloat(), px, py)
        canvas.drawBitmap(ring!!, matrix, null)
    }

    private fun onCreate(context: Context): Matrix {
        val size = width.coerceAtMost(height)
        ring = createBitmap(context, R.drawable.ring, size)
        transformation = Matrix()
        return transformation!!
    }

    private fun getAngle(x: Float, y: Float): Double {
        val cx = (width shr 1).toFloat()
        val cy = (height shr 1).toFloat()
        return -atan2((y - cy).toDouble(), (x - cx).toDouble()) * 180 / Math.PI + 90
    }

    companion object {

        private fun createBitmap(context: Context, resourceId: Int, size: Int): Bitmap {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            return Bitmap.createScaledBitmap(bitmap, size, size, false)
        }

        private fun ensureAngleInRange(delta: Double): Double {
            var x = delta
            while (x > 180) {
                x -= 360.0
            }
            while (x < -180) {
                x += 360.0
            }
            return x
        }
    }
}