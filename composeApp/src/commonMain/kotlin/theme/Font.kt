package theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.raleway_boldItalic
import mangarealm.composeapp.generated.resources.raleway_extraBold
import mangarealm.composeapp.generated.resources.raleway_italic_variableFont_wght
import mangarealm.composeapp.generated.resources.raleway_medium
import mangarealm.composeapp.generated.resources.raleway_semiBold
import mangarealm.composeapp.generated.resources.raleway_variableFont_wght
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ralewayFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.raleway_variableFont_wght),
        Font(Res.font.raleway_medium, weight = FontWeight.Medium),
        Font(Res.font.raleway_semiBold, weight = FontWeight.SemiBold),
        Font(Res.font.raleway_extraBold, weight = FontWeight.ExtraBold),
        Font(Res.font.raleway_italic_variableFont_wght, style = FontStyle.Italic),
        Font(Res.font.raleway_boldItalic, weight = FontWeight.Bold, style = FontStyle.Italic)
    )
}