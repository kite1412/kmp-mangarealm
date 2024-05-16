package shared

import MApp
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import io.github.irgaly.kottage.platform.KottageContext

@SuppressLint("ComposableNaming")
@Composable
actual fun adjustStatusBar(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
        }
    }
}

actual val kottageContext: KottageContext
    get() = MApp.kContext