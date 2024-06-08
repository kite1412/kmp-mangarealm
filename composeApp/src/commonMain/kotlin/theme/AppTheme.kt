package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import shared.adjustStatusBarColor

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    adjustStatusBarColor(Color(0xFFBFAA8C))
    MaterialTheme(
        colors = appColors,
        content = content,
        typography = Typography(
            defaultFontFamily = ralewayFamily(),
            body1 = TextStyle(color = Color.Black)
        )
    )
}