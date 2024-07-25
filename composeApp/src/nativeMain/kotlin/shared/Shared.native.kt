package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
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

@Composable
actual fun ZoomableImage(
    painter: Painter?,
    contentDescription: String,
    applyReadMode: Boolean,
    contentScale: ContentScale?,
    alignment: Alignment?,
    modifier: Modifier,
    onTap: ((Offset) -> Unit)?,
    onPainterNull: @Composable (() -> Unit)?
) {
}