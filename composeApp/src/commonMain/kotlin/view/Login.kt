package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.Roboto_Light
import mangarealm.composeapp.generated.resources.mangadex
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import theme.backgroundGrad
import theme.gradiant1

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to backgroundGrad[0],
                            0.65f to backgroundGrad[1]
                        ),
                        start = Offset(maxWidth.value / 2, 0.0f),
                        end = Offset(maxWidth.value / 2, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Welcome!",
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Login to your MangaDex account to continue",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(56.dp))
            LoginBar(onClick = {
                TODO("redirect to authorization point")
            })
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LoginBar (
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colorStops = arrayOf(
                0.15f to gradiant1[0],
                1f to gradiant1[1],
            )))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "Login with MangaDex",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(Res.font.Roboto_Light)),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            painter = painterResource(Res.drawable.mangadex),
            contentDescription = "MangaDex",
            modifier = Modifier.size(48.dp)
        )
    }
}