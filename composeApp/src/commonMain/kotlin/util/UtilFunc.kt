package util

import kotlin.math.PI
import kotlin.math.pow

fun circleArea(d: Float): Float {
    return (PI * ((d / 2).pow(2))).toFloat() / 10f
}
