package com.stho.myfling

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class CircleView : View {

    private var rotateListener: OnRotateListener? = null
    private var doubleTapListener: OnDoubleTapListener? = null

    private var gestureDetector: FlingingGestureDetector? = null
    private var angle = 13.0

    private var transformation: Matrix? = null
    private var ring: Bitmap? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupGestureDetector()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setupGestureDetector()
    }

    private fun setupGestureDetector() {
        gestureDetector = FlingingGestureDetector(context,CircleViewGestureListener(this, ::onRotate, ::onDoubleTap))
    }

    private fun onRotate(delta: Double) {
        rotateListener?.onRotate(delta)
    }

    private fun onDoubleTap() {
        doubleTapListener?.onDoubleTap()
    }

    fun setOnRotateListener(listener: OnRotateListener?) {
        rotateListener = listener
    }

    fun setOnDoubleTapListener(listener: OnDoubleTapListener?) {
        doubleTapListener = listener
    }

    fun setRotationAngle(newAngle: Double) {
        if (angle != newAngle) {
            angle = newAngle
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector?.onTouchEvent(event)
        return true
    }

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

    private fun ensureMatrix(): Matrix
            = transformation ?: createMatrix(context)

    private fun createMatrix(context: Context): Matrix {
        val size = width.coerceAtMost(height)
        ring = createBitmap(context, R.drawable.ring, size)
        transformation = Matrix()
        return transformation!!
    }

    companion object {

        private fun createBitmap(context: Context, resourceId: Int, size: Int): Bitmap {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            return Bitmap.createScaledBitmap(bitmap, size, size, false)
        }
    }
}