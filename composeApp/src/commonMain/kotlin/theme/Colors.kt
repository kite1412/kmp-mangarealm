package theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val gradient1 = listOf(Color(0xFF640D6B), Color(0xFFD11E1E))
val gradient2 = listOf(Color(0xFFA300B0), Color(0xFFE62323))
val lightBeige = Color(0xFFE8DED1)
val darkBeige = Color(0xFFBFAA8C)
val darkerBeige = Color(0xFF7A5E42)

val lightColors = lightColors(
    primary = Color(111, 78, 55),
    background = lightBeige,
    onBackground = darkBeige,
    secondary = Color(0xFF3881E5)
)

val darkColors = darkColors(
    primary = lightBeige,
    background = Color(0xFF121212),
    onBackground = darkerBeige,
    secondary = Color(0xFF3881E5)
)