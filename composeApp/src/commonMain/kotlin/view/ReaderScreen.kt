package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assets.`Chevron-right`
import assets.`Menu-outline`
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import shared.adjustStatusBarColor
import shared.disableEdgeToEdge
import util.APP_BAR_HEIGHT
import util.ImageQuality
import util.swipeToPop
import viewmodel.ReaderScreenModel

class ReaderScreen : Screen {
    @Composable
    private fun adjustStatusBar() {
        disableEdgeToEdge()
        adjustStatusBarColor(MaterialTheme.colors.onBackground)
    }

    @Composable
    override fun Content() {
        adjustStatusBar()
        val nav = LocalNavigator.currentOrThrow
        val sm = rememberScreenModel { ReaderScreenModel() }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .swipeToPop(nav)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (sm.imageQuality.isNotEmpty()) Column(modifier = Modifier.fillMaxSize()) {
                    TopBar(sm)

                }
                ImageQualityPrompt(sm)
            }
        }
    }

    @Composable
    private fun TopBar(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
                .background(MaterialTheme.colors.onBackground)
        ) {
            NavigationBar(
                sm = sm,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp)
            )
        }
    }

    @Composable
    private fun NavigationBar(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            Action(
                onClick = {

                },
                modifier = Modifier.fillMaxHeight().weight(0.15f)
            ) {
                Icon(
                    imageVector = Assets.`Chevron-right`,
                    contentDescription = "previous chapter",
                    tint = Color.White,
                    modifier = Modifier
                        .rotate(180f)
                        .align(Alignment.Center)
                )
            }
            Action(
                fill = false,
                onClick = {

                },
                modifier = Modifier.fillMaxSize().weight(0.7f)
            ) {
                Icon(
                    imageVector = Assets.`Menu-outline`,
                    contentDescription = "chapter list",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                )
                Text(
                    "Chapter ${sm.chapter.attributes.chapter!!}",
                    color = MaterialTheme.colors.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Action(
                onClick = {

                },
                modifier = Modifier.fillMaxHeight().weight(0.15f)
            ) {
                Icon(
                    imageVector = Assets.`Chevron-right`,
                    contentDescription = "previous chapter",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

    @Composable
    private fun ImageQualityPrompt(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(sm.imageQuality.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colors.background)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Image Quality",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ImageQualityButton(ImageQuality.HIGH) {
                                sm.imageQuality = ImageQuality.HIGH
                            }
                            ImageQualityButton(ImageQuality.DATA_SAVER) {
                                sm.imageQuality = ImageQuality.DATA_SAVER
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ImageQualityButton(
        label: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Action(
            onClick = onClick,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 12.dp)
            )
        }
    }

    @Composable
    private fun Reader(modifier: Modifier = Modifier) {

    }

    @Composable
    private fun Page(
        modifier: Modifier = Modifier
    ) {

    }
}