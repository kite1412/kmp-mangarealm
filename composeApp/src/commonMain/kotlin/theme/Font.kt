package theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.raleway_black
import mangarealm.composeapp.generated.resources.raleway_bold
import mangarealm.composeapp.generated.resources.raleway_extralight
import mangarealm.composeapp.generated.resources.raleway_italic
import mangarealm.composeapp.generated.resources.raleway_light
import mangarealm.composeapp.generated.resources.raleway_medium
import mangarealm.composeapp.generated.resources.raleway_regular
import mangarealm.composeapp.generated.resources.raleway_semibold
import mangarealm.composeapp.generated.resources.raleway_thin
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ralewayFamily(): FontFamily {
    val reg = Font(Res.font.raleway_regular)
    return FontFamily(
        reg,
        Font(Res.font.raleway_italic, style = FontStyle.Italic),
        Font(Res.font.raleway_light, weight = FontWeight.Light),
        Font(Res.font.raleway_extralight, weight = FontWeight.ExtraLight),
        Font(Res.font.raleway_bold, weight = FontWeight.Bold),
        Font(Res.font.raleway_medium, weight = FontWeight.Medium),
        Font(Res.font.raleway_thin, weight = FontWeight.Thin),
        Font(Res.font.raleway_black, weight = FontWeight.Black),
        Font(Res.font.raleway_semibold, weight = FontWeight.SemiBold)
    )
}