package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.irgaly.kottage.platform.KottageContext

@Composable
actual fun adjustStatusBar(color: Color) {
    TODO()
}

actual val kottageContext: KottageContext
    get() = KottageContext()