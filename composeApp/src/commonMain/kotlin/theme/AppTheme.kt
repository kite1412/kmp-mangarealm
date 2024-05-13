package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import shared.adjustStatusBar

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    adjustStatusBar(backgroundGrad[0])
    MaterialTheme(
        colors = appColors,
        content = content,
        typography = Typography(defaultFontFamily = ralewayFamily())
    )
}