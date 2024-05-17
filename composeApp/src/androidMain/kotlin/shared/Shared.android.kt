package shared

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.nrr.mangarealm.MainActivity
import io.github.irgaly.kottage.platform.KottageContext
import io.github.irgaly.kottage.platform.contextOf

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
    get() = contextOf(MainActivity.c)
actual val databaseDir: String
    get() = "${MainActivity.c.filesDir.path}/kottage"