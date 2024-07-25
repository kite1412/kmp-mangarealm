package shared

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.panpf.zoomimage.ZoomImage
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.zoom.ReadMode
import com.nrr.mangarealm.MainActivity
import io.github.irgaly.kottage.platform.KottageContext
import io.github.irgaly.kottage.platform.contextOf

@SuppressLint("ComposableNaming")
@Composable
actual fun adjustStatusBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
        }
    }
}

actual val kottageContext: KottageContext
    get() = contextOf(MainActivity.c)
actual val databaseDir: String
    get() = "${MainActivity.c.filesDir.path}/kottage"

@SuppressLint("ComposableNaming")
@Composable
actual fun applyEdgeToEdge() {
    MainActivity.c.enableEdgeToEdge()
}

@SuppressLint("ComposableNaming")
@Composable
actual fun adjustNavBarColor() {
    val color = if (isSystemInDarkTheme()) Color.Black else Color.White
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
        }
    }
}

actual val currentTimeMillis: Long
    get() = System.currentTimeMillis()

@SuppressLint("ComposableNaming")
@Composable
actual fun disableEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(MainActivity.c.window, true)
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
    if (painter != null) ZoomImage(
        painter = painter,
        state = rememberZoomState().apply {
            if (applyReadMode) zoomable.readMode = ReadMode.Default
        },
        contentDescription = contentDescription,
        contentScale = contentScale ?: ContentScale.Fit,
        onTap = onTap,
        alignment = alignment ?: Alignment.Center,
        modifier = modifier
    ) else onPainterNull?.invoke()
}