package com.stho.myfling

import android.animation.Animator
import android.os.Handler
import android.os.Looper
import android.view.View

class ViewAnimation private constructor(private val view: View) {

    private val handler = Handler(Looper.getMainLooper())

    fun cleanup() {
        handler.removeCallbacksAndMessages(null)
    }

    fun show() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ fadeIn() }, FADE_IN_DELAY.toLong())
    }

    fun dismiss() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ fadeOut() }, FADE_OUT_DELAY.toLong())
    }

    private fun fadeIn() {
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(FADE_IN_DURATION.toLong())
            .setListener(null)
    }

    private fun fadeOut() {
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(0f)
            .setDuration(FADE_OUT_DURATION.toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // ignore
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    // ignore
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // ignore
                }
            })
    }

    companion object {
        fun build(view: View): ViewAnimation {
            return ViewAnimation(view).also {
                view.visibility = View.INVISIBLE
                view.alpha = 0f
            }
        }

        private const val FADE_IN_DELAY = 10
        private const val FADE_IN_DURATION = 200
        private const val FADE_OUT_DELAY = 300
        private const val FADE_OUT_DURATION = 500
    }
}