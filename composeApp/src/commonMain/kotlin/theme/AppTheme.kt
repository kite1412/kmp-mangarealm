package theme

import LocalSharedViewModel
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val appSettings = LocalSharedViewModel.current.appSettings
    val isDarkTheme = appSettings.isDarkMode.value
    MaterialTheme(
        colors = if (!isDarkTheme) lightColors else darkColors,
        content = content,
        typography = Typography(
            defaultFontFamily = ralewayFamily(),
            body1 = TextStyle(color = if (!isDarkTheme) Color.Black else Color.White)
        )
    )
}