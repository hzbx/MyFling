package com.stho.myfling

import kotlin.math.IEEErem

/**
 * Created by shoedtke on 30.08.2016.
 */
object Degree {
    fun sin(degree: Double): Double =
        kotlin.math.sin(Math.toRadians(degree))

    fun cos(degree: Double): Double =
        kotlin.math.cos(Math.toRadians(degree))

    fun arcTan2(y: Double, x: Double): Double =
        Math.toDegrees(kotlin.math.atan2(y, x))

    fun arcCos(x: Double): Double =
        Math.toDegrees(kotlin.math.acos(x))

    fun normalize(degree: Double): Double =
        degree.IEEErem(360.0).let {
            when {
                it < 0 -> it + 360.0
                else -> it
            }
        }

    fun normalizeTo180(degree: Double): Double =
        degree.IEEErem(360.0).let {
            when {
                it > 180 -> it - 360.0
                it < -180 -> it + 360.0
                else -> it
            }
        }
}

