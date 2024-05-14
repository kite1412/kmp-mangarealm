package view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.Roboto_Light
import mangarealm.composeapp.generated.resources.mangadex
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import theme.backgroundGrad
import theme.gradient1
import theme.gradient2
import viewmodel.LoginViewModel

private fun background(maxWidth: Dp) = Brush.linearGradient(
    colorStops = arrayOf(
        0f to backgroundGrad[0],
        0.65f to backgroundGrad[1]
    ),
    start = Offset(maxWidth.value / 2, 0.0f),
    end = Offset(maxWidth.value / 2, Float.POSITIVE_INFINITY)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    vm: LoginViewModel = LoginViewModel.Factory.create()
) {
    val state = vm.pagerState
    val scope = rememberCoroutineScope()
    HorizontalPager(state, userScrollEnabled = false) {
        when (it) {
            0 -> BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(
                            brush = background(maxWidth)
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
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(56.dp))
                    LoginBar(onClick = {
                       scope.launch {
                           state.animateScrollToPage(1)
                       }
                    })
                }
            }
            1 -> PromptPage(onClick = {})
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LoginBar (
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colorStops = arrayOf(
                0.15f to gradient1[0],
                1f to gradient1[1],
            )))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
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
        Icon(
            Icons.AutoMirrored.Default.KeyboardArrowRight,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-12).dp),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun PromptPage(onClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = background(maxWidth))
                .padding(start = 24.dp, end = 24.dp, top = 96.dp)
        ) {
            Text(
                "Login",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            MTextField("Username", "asd")
            MTextField("Password", "asd", modifier = Modifier.padding(top = 8.dp))
            MTextField("Client Id", "asd", modifier = Modifier.padding(top = 8.dp))
            MTextField("Client Secret", "asd", modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 14.dp),
            colors = ButtonDefaults.buttonColors(
                // make a const
                backgroundColor = Color(0xFF3881E5),
                contentColor = Color.White
            ),
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Login", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = "login",
                    modifier = Modifier
                        .size(14.dp)
                        .offset(y = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun MTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            modifier = Modifier.offset(x = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
        BasicTextField(
            value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = (1.2).dp,
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.3f to gradient2[0],
                            1f to gradient2[1]
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            cursorBrush = SolidColor(Color.White),
            textStyle = TextStyle(color = Color.White)
        )
    }
}