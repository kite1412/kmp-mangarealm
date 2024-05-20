package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Token
import assets.Eye
import assets.Eyeshut
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
    vm: LoginViewModel = LoginViewModel.Factory.create(),
    onSuccess: (Token) -> Unit
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
            1 -> PromptPage(
                vm = vm,
                onClick = { vm.onTap(onSuccess) }
            )
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
private fun PromptPage(
    onClick: () -> Unit,
    vm: LoginViewModel
) {
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
            MTextField(
                label = "Username",
                value = vm.username.value,
                onValueChange = { vm.setUsername(it) }
            )
            MTextField(
                label = "Password",
                value = vm.password.value,
                isSensitive = true,
                onValueChange = { vm.setPassword(it) },
                modifier = Modifier.padding(top = 8.dp)
            )
            MTextField(
                label = "Client Id",
                value = vm.clientId.value,
                onValueChange = { vm.setClientId(it) },
                modifier = Modifier.padding(top = 8.dp)
            )
            MTextField(
                label = "Client Secret",
                value = vm.clientSecret.value,
                isSensitive = true,
                onValueChange = { vm.setClientSecret(it) },
                modifier = Modifier.padding(top = 8.dp)
            )
            if (vm.loading.value) CircularIndicator(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
        AnimatedVisibility(
            vm.triggerMessage.value,
            enter = slideInVertically { 40 },
            exit = ExitTransition.None,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            MessageBar(vm.success.value)
        }
        AnimatedVisibility(
            !vm.success.value,
            exit = ExitTransition.None,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 14.dp),
        ) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    // make a const
                    backgroundColor = Color(0xFF3881E5),
                    contentColor = Color.White
                ),
                enabled = vm.enableTap.value,
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
}

// TODO: add visual transformation on sensitive field
@Composable
private fun MTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isSensitive: Boolean = false,
    modifier: Modifier = Modifier
) {
    var hide by remember {
        mutableStateOf(true)
    }
    Column(modifier = modifier) {
        Text(
            label,
            modifier = Modifier.offset(x = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = (1.6).dp,
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.3f to gradient2[0],
                            1f to gradient2[1]
                        )
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            BasicTextField(
                value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                cursorBrush = SolidColor(Color.White),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isSensitive) KeyboardType.Password
                        else KeyboardType.Text
                ),
                visualTransformation = if (isSensitive) if (hide) SensitiveFieldTransformation()
                    else VisualTransformation.None else VisualTransformation.None
            )
            if (isSensitive) IconButton(onClick = {
                hide = !hide
            }) {
                Icon(
                    imageVector = if (hide) Assets.Eye else Assets.Eyeshut,
                    contentDescription = if (hide) "show" else "hide",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun CircularIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(255, 255, 255, 230))
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(28.dp),
            color = Color.Black,
            strokeWidth = 8.dp
        )
    }
}

@Composable
private fun MessageBar(
    success: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            // make const
            .background(if (success) Color(0xFF006400) else Color(0xFFbf0000))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                if (success) "Logged in" else "Fail to login",
                color = Color.White
            )
            Spacer(modifier = Modifier.width(1.dp))
            Icon(
                if (success) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

private class SensitiveFieldTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(text.map { '*' }.joinToString(separator = "")),
            offsetMapping = OffsetMapping.Identity
        )
    }
}