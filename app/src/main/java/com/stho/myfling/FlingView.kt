package com.stho.myfling

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class FlingView : View {

    private var rotateListener: OnRotateListener? = null
    private var doubleTapListener: OnDoubleTapListener? = null
    private var gestureDetector: FlingingGestureDetector? = null
    private var angle: Double = 0.0

    private val green: Paint = Paint().apply {
        this.color = Color.argb(255, 34, 102, 59)
        this.strokeWidth = 1f
        this.style = Paint.Style.FILL_AND_STROKE
    }
    private val gray: Paint = Paint().apply {
        this.color = Color.argb(150, 90, 90, 90)
        this.strokeWidth = 2f
        this.style = Paint.Style.STROKE
    }
    private val path: Path = Path()

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupGestureDetector()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupGestureDetector()
    }

    /**
     * Note, there are two ways of providing the lambda functions onRotate and onDoubleTap
     *  either like
     *  (1) rotate = { delta -> rotateListener?.onRotate(delta) },
     *  or like
     *  (2) rotate = ::onRotate,
     *  with
     *      private fun onRotate(delta: Double) {
     *          rotateListener?.onRotate(delta)
     *      }
     */
    private fun setupGestureDetector() {
        gestureDetector = FlingingGestureDetector(
            context,
            listener = FlingViewGestureListener(
                view = this,
                rotate = { delta -> rotateListener?.onRotate(delta) },
                doubleTap = { doubleTapListener?.onDoubleTap() },
            ),
        )
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

        path.reset()
        val h = height.toFloat()
        val w = width.toFloat()
        val l = h / 2
        for (alpha in 15..360 step 30) {
            val beta = Degree.normalizeTo180(angle - alpha)
            if (-90.0 <= beta && beta <= 90.0) {
                val sinBeta = Degree.sin(beta).toFloat()
                val y = l * (1 - sinBeta)
                val x = 0f
                path.moveTo(x, y)
                path.lineTo(w, y)
            }
        }
        canvas.drawPath(path, gray)

        val projectionRadius = 5 * l
        green.textSize = resources.getDimensionPixelSize(R.dimen.flingFontSize).toFloat()
        for (alpha in 0..350 step 30) {
            val beta = Degree.normalizeTo180(angle - alpha)
            if (-90.0 <= beta && beta <= 90.0) {
                val cosBeta = Degree.cos(beta).toFloat()
                val sinBeta = Degree.sin(beta).toFloat()
                val x = w / 2
                val y = l * (1 - sinBeta)
                val text = alpha.toString()
                val factor = (projectionRadius + l * cosBeta) / (projectionRadius + l)
                val textWith = green.measureText(text)
                val textSize = green.textSize
                canvas.save()
                canvas.scale(factor, factor * cosBeta, x, y)
                canvas.drawText(alpha.toString(), x - textWith / 2, y + textSize / 2, green)
                canvas.restore()
            }
        }
    }
}