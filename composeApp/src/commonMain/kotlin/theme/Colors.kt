package theme

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val backgroundGrad = listOf(Color(0xFF421313), Color(0xFFA83131))

val gradient1 = listOf(Color(0xFF640D6B), Color(0xFFD11E1E))
val gradient2 = listOf(Color(0xFFA300B0), Color(0xFFE62323))

val appColors = lightColors(
    primary = Color(0xFFAA0000),
    background = Color(0xFF292929),
    onBackground = Color(0xFF232323)
)

val Colors.primaryForThick: Color
    get() = Color(145, 0,0, 255)