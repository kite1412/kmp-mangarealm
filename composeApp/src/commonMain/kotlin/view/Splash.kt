package view

import Assets
import LocalScreenSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assets.`Mr-logo`
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.Roboto_Thin
import mangarealm.composeapp.generated.resources.mangadex
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import theme.darkBeige
import theme.lightBeige

@Composable
fun SplashScreen() {
    val screenSize = LocalScreenSize.current
    val xStart = with(LocalDensity.current) {
        screenSize.width.toPx() / 2 - 50.dp.toPx()
    }
    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) {}
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.5f to darkBeige,
                        1f to lightBeige,
                    ),
                    start = Offset(xStart, 0f)
                )
            ),
    ) {
        Image(
            imageVector = Assets.`Mr-logo`,
            contentDescription = "Manga Realm",
            modifier = Modifier
                .height(100.dp)
                .width((100 * 2.5).dp)
                .align(Alignment.Center)
                .offset(y = (-80).dp)
        )
        Credit(modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = (-24).dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Credit(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Credit to",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(Res.font.Roboto_Thin))
        )
        Image(
            painter = painterResource(Res.drawable.mangadex),
            contentDescription = "MangaDex",
            modifier = Modifier
                .size(40.dp)
        )
    }
}