package util

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import shared.adjustNavBarColor
import shared.adjustStatusBarColor
import shared.applyEdgeToEdge
import shared.disableEdgeToEdge
import kotlin.math.PI
import kotlin.math.pow

fun circleArea(d: Float): Float {
    return (PI * ((d / 2).pow(2))).toFloat() / 10f
}

@Composable
fun edgeToEdge() {
    applyEdgeToEdge()
    adjustNavBarColor()
}

@Composable
fun undoEdgeToEdge() {
    disableEdgeToEdge()
    adjustStatusBarColor(MaterialTheme.colors.background)
}

fun <K, V> List<Map<K, V>>.toMap(): Map<K, V> {
    val m = mutableMapOf<K, V>()
    forEach {
        m[it.keys.first()] = it[it.keys.first()]!!
    }
    return m
}