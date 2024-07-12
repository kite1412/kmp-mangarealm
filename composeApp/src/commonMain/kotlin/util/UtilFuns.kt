package util

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.navigator.Navigator
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

fun Modifier.swipeToPop(nav: Navigator): Modifier = pointerInput(true) {
    detectHorizontalDragGestures { _, dragAmount ->
        if (dragAmount > 30) nav.pop()
    }
}

suspend fun <R> retry(
    count: Int,
    predicate: (R) -> Boolean,
    block: suspend (Int) -> R
): R {
    val r = block(count)
    return if (count > 1) if (predicate(r)) retry(count - 1, predicate, block)
        else r else r
}

fun publicationStatusColor(rawStatus: String): Color = when(rawStatus) {
    PublicationStatus.ON_GOING -> Color(0xFF1B663E)
    PublicationStatus.COMPLETED -> Color( 46, 90, 180)
    PublicationStatus.HIATUS -> Color.DarkGray
    PublicationStatus.CANCELLED -> Color(150, 0, 0)
    else -> Color.LightGray
}

fun publicationStatus(raw: String): String = when(raw) {
    PublicationStatus.ON_GOING -> "On Going"
    PublicationStatus.COMPLETED -> "Completed"
    PublicationStatus.HIATUS -> "Hiatus"
    PublicationStatus.CANCELLED -> "Cancelled"
    else -> "Unknown"
}

fun publicationDemographic(raw: String): String = when(raw) {
    PublicationDemographic.SHOUNEN -> "Shounen"
    PublicationDemographic.SHOUJO -> "Shoujo"
    PublicationDemographic.JOSEI -> "Josei"
    PublicationDemographic.SEINEN -> "Seinen"
    else -> "Unknown"
}

fun publicationDemographicColor(raw: String): Color = when(raw) {
    PublicationDemographic.SHOUNEN -> Color(0xFFD32F2F)
    PublicationDemographic.SHOUJO -> Color(0xFFFFB6C1)
    PublicationDemographic.JOSEI -> Color(0xFFB39DDB)
    PublicationDemographic.SEINEN -> Color(0xFF7986CB)
    else -> Color.Transparent
}