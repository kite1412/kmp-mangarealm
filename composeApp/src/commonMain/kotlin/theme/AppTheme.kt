package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import shared.adjustStatusBarColor

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    adjustStatusBarColor(Color(0xFFBFAA8C))
    MaterialTheme(
        colors = appColors,
        content = content,
        typography = Typography(defaultFontFamily = ralewayFamily())
    )
}