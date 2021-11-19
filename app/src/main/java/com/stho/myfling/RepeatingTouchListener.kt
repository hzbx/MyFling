package com.stho.myfling

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*


/**
 * Implement a repeating click listener
 *
 * When you click the "button" just once, one click event is fired.
 * When you press the "button" down, repeated click events are fired, until you release the button.
 * The first click event is fired immediately
 * The second click event is fired after the initial interval.
 * Subsequent click events are fired every repeating interval.
 *
 * Register the listener for a layout item "anyView" as follows:
 *      binding.anyView.setOnTouchListener(RepeatingTouchListener({ anyAction() }, 700, 70))
 *
 * The intervals are specified in milliseconds.
 */
class RepeatingTouchListener(
    private val listener: View.OnClickListener,
    private val initialInterval: Long = 500L,
    private val repeatingInterval: Long = 50L,
) : View.OnTouchListener {

    private var touchedView: View? = null
    private var job: Job? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchedView = view
                job = startNewJob(view)
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                job?.cancel()
                touchedView?.isPressed = false
                touchedView = null
                true
            }
            else -> {
                false
            }
        }

    private fun startNewJob(view: View): Job =
        CoroutineScope(Dispatchers.Main).launch {
            view.isPressed = true
            listener.onClick(view)
            delay(initialInterval)
            while (view.isEnabled) {
                listener.onClick(view)
                delay(repeatingInterval)
            }
            view.isPressed = false
        }

}


