package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assets.`Chevron-right`
import assets.`Collapse-solid`
import assets.`Expand-solid`
import assets.Height
import assets.`Menu-outline`
import assets.Width
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import screenSize
import shared.ZoomableImage
import shared.adjustStatusBarColor
import shared.disableEdgeToEdge
import util.APP_BAR_HEIGHT
import util.ImageQuality
import util.swipeToPop
import viewmodel.Layout
import viewmodel.LayoutBarStatus
import viewmodel.ReaderScreenModel

private val BACKGROUND_COLOR = Color(40, 40, 40)

class ReaderScreen : Screen {
    @Composable
    private fun adjustStatusBar() {
        disableEdgeToEdge()
        adjustStatusBarColor(Color.DarkGray)
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(true) {
                        detectTapGestures { sm.handleLayoutBar() }
                    }
            ) {
                if (!sm.showPrompt) Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TopBar(sm)
                    ChaptersView(
                        sm = sm,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = APP_BAR_HEIGHT)
                    )
                    LayoutBar(
                        sm = sm,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                    )
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
                .background(BACKGROUND_COLOR)
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
        AnimatedVisibility(
            visible = sm.imageQuality.isEmpty() && sm.showPrompt,
            modifier = modifier
                .fillMaxSize()
                .background(BACKGROUND_COLOR)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colors.background)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Images Quality",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Choose chapter's images quality",
                            color = Color.DarkGray
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImageQualityButton(
                            label = ImageQuality.HIGH,
                            onClick = sm::onPromptClick
                        )
                        ImageQualityButton(
                            label = ImageQuality.DATA_SAVER,
                            onClick = sm::onPromptClick
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ImageQualityButton(
        label: String,
        modifier: Modifier = Modifier,
        onClick: (String) -> Unit
    ) {
        Action(
            onClick = { onClick(label) },
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
    private fun ChaptersView(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        painterLoaders(sm)
        Box(modifier = modifier) {
            ColumnLayout(sm)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ColumnLayout(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        if (sm.painters.isNotEmpty()) if (sm.zoomIn) VerticalPager(
            state = rememberPagerState { sm.painters.size },
            modifier = modifier
        ) {
            ZoomedInChapter(sm.painters[it]) { sm.handleLayoutBar() }
        } else LazyColumn(modifier = modifier) {
            items(sm.painters) {
                ZoomedInChapter(it)
            }
        }
    }

    @Composable
    private fun RowLayout(modifier: Modifier = Modifier) {

    }

    @Composable
    private fun ZoomedInChapter(
        painter: Painter?,
        modifier: Modifier = Modifier,
        onTap: ((Offset) -> Unit)? = null
    ) {
        val common = Modifier
            .fillMaxSize()
            .background(BACKGROUND_COLOR)
        ZoomableImage(
            painter = painter,
            onTap = onTap,
            modifier = modifier.then(common)
        ) {
            Box(modifier = modifier.then(common)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                    Text(
                        "loading chapter...",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    fun painterLoaders(sm: ReaderScreenModel) {
        if (sm.imageFiles.isNotEmpty()) for ((i, filename) in sm.imageFiles.withIndex())
            PainterLoader(
                url = filename,
                onImageLoaded = { p ->
                    sm.painters[i] = p
                }
            )
    }

    @Composable
    private fun LayoutBar(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        val show = sm.showLayoutBar == LayoutBarStatus.SHOW || sm.showLayoutBar == LayoutBarStatus.UPDATE
        LaunchedEffect(sm.showLayoutBar) {
            if (show) delay(4000)
            sm.showLayoutBar = LayoutBarStatus.HIDE
        }
        val offset by animateDpAsState(if (show) 0.dp else 88.dp)
        Box(
            modifier = modifier
                .offset(y = offset)
                .height(APP_BAR_HEIGHT)
                .width(screenSize.width / 1.4f)
                .clip(CircleShape)
                .background(MaterialTheme.colors.onBackground)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                LayoutButton(
                    imageVector = Assets.Height,
                    layout = Layout.COLUMN,
                    selected = sm.defaultLayout,
                    onClick = sm::changeLayout,
                    modifier = Modifier.weight(0.4f)
                )
                LayoutButton(
                    imageVector = Assets.Width,
                    layout = Layout.ROW,
                    selected = !sm.defaultLayout,
                    onClick = sm::changeLayout,
                    applySpace = true,
                    modifier = Modifier.weight(0.4f)
                )
                ZoomMode(sm.zoomIn, modifier = Modifier.weight(0.2f)) {}
            }
        }
    }

    @Composable
    private fun LayoutButton(
        imageVector: ImageVector,
        layout: Layout,
        selected: Boolean,
        // space between icon and text
        applySpace: Boolean = false,
        modifier: Modifier = Modifier,
        onClick: (Layout) -> Unit
    ) {
        val bgColor by animateColorAsState(if (selected) MaterialTheme.colors.secondary else Color.Transparent)
        val contentColor by animateColorAsState(if (selected) Color.White else Color.DarkGray)
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(bgColor)
                .clickable(onClick = { onClick(layout) })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = layout.toString(),
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                if (applySpace) Spacer(Modifier.width(2.dp))
                Text(
                    layout.toString(),
                    color = contentColor,
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    @Composable
    private fun ZoomMode(
        zoomedIn: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val icon = if (zoomedIn) Assets.`Collapse-solid` else Assets.`Expand-solid`
        Icon(
            imageVector = icon,
            contentDescription = if (zoomedIn) "zoom out" else "zoom in",
            tint = Color.DarkGray,
            modifier = modifier
                .size(30.dp)
                .clickable(onClick = onClick)
        )
    }
}