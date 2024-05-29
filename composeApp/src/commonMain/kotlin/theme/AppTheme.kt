package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import shared.adjustStatusBarColor

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    adjustStatusBarColor(backgroundGrad[0])
    MaterialTheme(
        colors = appColors,
        content = content,
        typography = Typography(defaultFontFamily = ralewayFamily())
    )
}