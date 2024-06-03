package theme

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val gradient1 = listOf(Color(0xFF640D6B), Color(0xFFD11E1E))
val gradient2 = listOf(Color(0xFFA300B0), Color(0xFFE62323))

val appColors = lightColors(
    primary = Color.Black,
    background = Color(0xFFE8DED1),
    onBackground = Color(0xFFBFAA8C)
)

val Colors.unselectedButton: Color
    get() = Color(0xFFD1C5B4)

val Colors.selectedButton: Color
    get() = Color(0xFF322C00)