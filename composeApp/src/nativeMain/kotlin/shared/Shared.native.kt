package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.irgaly.kottage.platform.KottageContext

@Composable
actual fun adjustStatusBarColor(color: Color) {
    TODO()
}

actual val kottageContext: KottageContext
    get() = KottageContext()
actual val databaseDir: String
    get() = TODO("Not yet implemented")

@Composable
actual fun applyEdgeToEdge() {
}

@Composable
actual fun adjustNavBarColor() {
}

actual val currentTimeMillis: Long
    get() = TODO("Not yet implemented")

@Composable
actual fun disableEdgeToEdge() {
}