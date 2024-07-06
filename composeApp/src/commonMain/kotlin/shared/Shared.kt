package shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import io.github.irgaly.kottage.platform.KottageContext

@Composable
expect fun adjustStatusBarColor(color: Color)

expect val kottageContext: KottageContext

expect val databaseDir: String

@Composable
expect fun applyEdgeToEdge()

@Composable
expect fun disableEdgeToEdge()

@Composable
expect fun adjustNavBarColor()

expect val currentTimeMillis: Long

@Composable
expect fun ZoomableImage(
    painter: Painter?,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    onTap: ((Offset) -> Unit)? = null,
    onPainterNull: @Composable (() -> Unit)? = null,
)