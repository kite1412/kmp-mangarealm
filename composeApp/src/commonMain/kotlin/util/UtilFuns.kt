package util

import LocalScreenSize
import LocalSharedViewModel
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import api.jikan.model.Character
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import shared.adjustNavBarColor
import shared.adjustStatusBarColor
import shared.applyEdgeToEdge
import shared.disableEdgeToEdge

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

@Composable
fun undoEdgeToEdge(statusBarColor: Color) {
    disableEdgeToEdge()
    adjustStatusBarColor(statusBarColor)
}

fun <K, V> List<Map<K, V>>.toMap(): Map<K, V> {
    val m = mutableMapOf<K, V>()
    forEach {
        m[it.keys.first()] = it[it.keys.first()]!!
    }
    return m
}

fun Modifier.swipeToPop(nav: Navigator? = null, enabled: Boolean = true, action: () -> Unit = {}): Modifier = composed {
    val enableSwipeToPop by LocalSharedViewModel.current.appSettings.enableSwipeToPop
    val swipeToPop = enableSwipeToPop && enabled
    pointerInput(swipeToPop) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount > 70 && swipeToPop)
                if (nav == null) action() else nav.pop()
        }
    }
}

suspend fun <R> retry(
    maxAttempts: Int,
    predicate: (R) -> Boolean,
    block: suspend (Int) -> R
): R {
    var attemptCount = 1
    val initialDelay = 500L
    var attempt: R = block(attemptCount)
    while (attemptCount < maxAttempts && predicate(attempt)) {
        delay(initialDelay * attemptCount)
        attemptCount++
        attempt = block(attemptCount)
    }
    return attempt
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

fun defaultMangaRequestQueries(queries: Map<String, Any> = mapOf()): Map<String, Any> = mutableMapOf(
    "limit" to DEFAULT_COLLECTION_SIZE,
    "includes[]" to listOf("cover_art", "author"),
).also { it.putAll(queries) }

@Composable
fun isDarkMode(): Boolean = LocalSharedViewModel.current.appSettings.isDarkMode.value

@Composable
fun appGray(): Color = if (isDarkMode())
    Color.Gray else Color.DarkGray

@Composable
fun reverseAppGray(): Color = if (isDarkMode())
    Color.DarkGray else Color.Gray

fun resolveCharacterPicture(character: Character): String =
    character.character.images.jpg.largeImageUrl
        ?: character.character.images.jpg.imageUrl
        ?: character.character.images.jpg.smallImageUrl
        ?: ""

@Composable
fun getMaxDimension(): Dp = with(LocalScreenSize.current) {
    if (height >= width) height else width
}

@Composable
fun getMinDimension(): Dp = with(LocalScreenSize.current) {
    if (height >= width) width else height
}