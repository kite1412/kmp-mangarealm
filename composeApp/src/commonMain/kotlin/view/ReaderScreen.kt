package view

import Assets
import LocalScreenSize
import LocalSharedViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.util.getChapterImageUrl
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
import kotlinx.coroutines.launch
import shared.ZoomableImage
import shared.adjustStatusBarColor
import shared.disableEdgeToEdge
import util.APP_BAR_HEIGHT
import util.ImageQuality
import util.appGray
import util.swipeToPop
import view_model.Layout
import view_model.LayoutBarStatus
import view_model.ReaderScreenModel

private val BACKGROUND_COLOR = Color(30, 30, 30)

class ReaderScreen : Screen {
    @Composable
    private fun adjustStatusBar() {
        disableEdgeToEdge()
        adjustStatusBarColor(BACKGROUND_COLOR)
    }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { ReaderScreenModel(sharedViewModel) }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .swipeToPop(nav)
        ) {
            adjustStatusBar()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BACKGROUND_COLOR)
            ) {
                if (!sm.showPrompt) Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val screenSize = LocalScreenSize.current
                    PagesView(
                        sm = sm,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = APP_BAR_HEIGHT)
                    )
                    BottomBar(sm, modifier = Modifier.align(Alignment.BottomCenter))
                    LayoutBar(
                        sm = sm,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp + APP_BAR_HEIGHT)
                    )
                    ChapterList(
                        sm = sm,
                        modifier = Modifier
                            .width(screenSize.width * 0.7f)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = APP_BAR_HEIGHT)
                    )
                }
                ImageQualityPrompt(sm)
            }
        }
    }

    @Composable
    private fun BottomBar(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
                .background(Color.Transparent)
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
                enabled = sm.currentChapterIndex > 0,
                onClick = { sm.onChapterClick(sm.currentChapterIndex - 1) },
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
                onClick = { sm.showChapterList = !sm.showChapterList },
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
                    "Chapter ${sm.chapters[sm.currentChapterIndex]().attributes.chapter ?: (sm.currentChapterIndex + 1)}",
                    color = MaterialTheme.colors.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Action(
                enabled = sm.currentChapterIndex < sm.chapters.size - 1,
                onClick = { sm.onChapterClick(sm.currentChapterIndex + 1) },
                modifier = Modifier.fillMaxHeight().weight(0.15f)
            ) {
                Icon(
                    imageVector = Assets.`Chevron-right`,
                    contentDescription = "next chapter",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

    @Composable
    private fun ChapterList(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = sm.showChapterList,
            modifier = modifier
        ) {
            val screenSize = LocalScreenSize.current
            val listHeight = screenSize.height / 3.dp
            val lazyListState = rememberLazyListState(
                initialFirstVisibleItemIndex = sm.currentChapterIndex,
                initialFirstVisibleItemScrollOffset = -(listHeight.toInt())
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .height(listHeight.dp)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            ) {
                val chapters = sm.chapters
                items(chapters.size) {
                    Chapter(
                        chapter = "Chapter ${chapters[it]().attributes.chapter ?: (sm.currentChapterIndex + 1)}",
                        selected = it == sm.currentChapterIndex
                    ) { sm.onChapterClick(it) }
                }
            }
        }
    }

    @Composable
    private fun Chapter(
        chapter: String,
        selected: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val color = if (selected) MaterialTheme.colors.secondary else Color.Black
        TextButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                chapter,
                fontWeight = FontWeight.Medium,
                color = color,
            )
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
                            color = appGray()
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
    private fun PagesView(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        Box(modifier = modifier) {
            ColumnLayout(sm, modifier = Modifier.align(Alignment.Center))
            if (sm.showWarning) Text(
                "No pages found",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
            AnimatedVisibility(
                visible = sm.showPageIndicator,
                modifier = Modifier
                    .offset(y = -(24).dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                PageIndicator(sm, Color.White)
            }
        }
    }

    @Composable
    private fun PageImageLoader(
        sm: ReaderScreenModel,
        index: Int,
        applyReadMode: Boolean = true,
        modifier: Modifier = Modifier,
        onTap: ((Offset) -> Unit)? = null
    ) {
        if (sm.images.isNotEmpty()) {
            val image = sm.images[index]
            if (image.painter == null) PainterLoader(
                url = getChapterImageUrl(
                    baserUrl = image.baseUrl,
                    hash = image.hash,
                    imageQuality = ImageQuality(image.quality),
                    filename = image.fileUrl
                )
            ) {
                sm.updatePainter(index, it)
            }
            Page(
                painter = image.painter,
                onTap = onTap,
                applyReadMode = applyReadMode,
                contentDescription = image.fileUrl,
                modifier = modifier
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ColumnLayout(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        val currentIndex = sm.currentPage - 1
        val pagerState = rememberPagerState(initialPage = currentIndex) { sm.images.size }
        val state = rememberLazyListState(initialFirstVisibleItemIndex = currentIndex)
        LaunchedEffect(sm.pageNavigatorIndex) {
            if (sm.updateFromNavigator) {
                sm.updateFromNavigator = false
                delay(500)
                sm.currentPage = sm.pageNavigatorIndex + 1
                if (sm.zoomIn) pagerState.scrollToPage(sm.pageNavigatorIndex)
                    else state.scrollToItem(sm.pageNavigatorIndex)
            }
        }
        LaunchedEffect(sm.zoomIn, sm.index) {
            if (sm.zoomIn) pagerState.scrollToPage(currentIndex)
                else state.scrollToItem(currentIndex)
        }
        if (sm.images.isNotEmpty()) if (sm.zoomIn) SwipeableColumnLayout(sm, pagerState, modifier)
            else ScrollableColumnLayout(sm, state, modifier)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun SwipeableColumnLayout(
        sm: ReaderScreenModel,
        pagerState: PagerState,
        modifier: Modifier = Modifier
    ) {
        LaunchedEffect(pagerState.currentPage) {
            sm.currentPage = pagerState.currentPage + 1
            sm.togglePageIndicator()
        }
        VerticalPager(
            state = pagerState,
            modifier = modifier,
            beyondBoundsPageCount = 1
        ) {
            PageImageLoader(
                sm = sm,
                index = it,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) { sm.handleLayoutBar() }
        }
    }

    @Composable
    private fun ScrollableColumnLayout(
        sm: ReaderScreenModel,
        state: LazyListState,
        modifier: Modifier = Modifier
    ) {
        BoxWithConstraints(modifier = modifier) {
            val imageHeight = maxHeight / 1.5f
            val density = LocalDensity.current
            val pageFraction = imageHeight / 3
            val pageChanged by remember {
                derivedStateOf {
                    with(density) {
                        state.firstVisibleItemScrollOffset.toDp() <= pageFraction
                    }
                }
            }
            LaunchedEffect(pageChanged) {
                if (pageChanged) sm.currentPage = state.firstVisibleItemIndex + 1
            }
            LazyColumn(
                state = state,
                contentPadding = PaddingValues(vertical = pageFraction),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sm.images.size) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(imageHeight)
                    ) {
                        Text(
                            "${it + 1} / ${sm.images.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .padding(vertical = 2.dp)
                        )
                        PageImageLoader(
                            sm = sm,
                            index = it,
                            applyReadMode = false,
                            modifier = Modifier.fillMaxSize()
                        ) { sm.handleLayoutBar() }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowLayout(modifier: Modifier = Modifier) {

    }

    @Composable
    private fun Page(
        painter: Painter?,
        contentDescription: String = "",
        applyReadMode: Boolean = true,
        modifier: Modifier = Modifier,
        onTap: ((Offset) -> Unit)? = null
    ) {
        ZoomableImage(
            painter = painter,
            onTap = onTap,
            applyReadMode = applyReadMode,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = modifier.clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { onTap?.invoke(Offset.Zero) }
                )
            ) {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        "loading page...",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    private fun LayoutBar(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        val show = sm.showLayoutBar == LayoutBarStatus.SHOW || sm.showLayoutBar == LayoutBarStatus.UPDATE
        LaunchedEffect(sm.showLayoutBar, sm.layoutBarDismissible) {
            if (sm.layoutBarDismissible) {
                if (show) delay(4000)
                sm.showLayoutBar = LayoutBarStatus.HIDE
            }
        }
        val height = APP_BAR_HEIGHT / 1.4f
        val offset by animateDpAsState(if (show) 0.dp else 24.dp + APP_BAR_HEIGHT + height)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .offset(y = offset)
        ) {
            val screenSize = LocalScreenSize.current
            AnimatedVisibility(
                visible = sm.showPageNavigator,
                modifier = Modifier.height(height)
            ) {
                PageNavigator(sm)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(APP_BAR_HEIGHT / 1.4f)
                    .width(screenSize.width)
                    .padding(horizontal = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .clickable(enabled = false) {}
            ) {
                LayoutButton(
                    imageVector = Assets.Height,
                    layout = Layout.COLUMN,
                    selected = sm.defaultLayout,
                    onClick = sm::changeLayout,
                    modifier = Modifier.weight(0.3f)
                )
                LayoutButton(
                    imageVector = Assets.Width,
                    layout = Layout.ROW,
                    selected = !sm.defaultLayout,
                    onClick = sm::changeLayout,
                    applySpace = true,
                    modifier = Modifier.weight(0.3f)
                )
                ZoomMode(sm.zoomIn, modifier = Modifier.weight(0.1f)) {
                    sm.zoomIn = !sm.zoomIn
                }
                PageIndicator(
                    sm = sm,
                    modifier = Modifier
                        .weight(0.3f)
                        .clickable {
                            sm.handlePageNavigator(showNavigator = true)
                        }
                )
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
        val icon = if (zoomedIn) Assets.`Expand-solid` else Assets.`Collapse-solid`
        Icon(
            imageVector = icon,
            contentDescription = if (zoomedIn) "zoom out" else "zoom in",
            tint = Color.Gray,
            modifier = modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        )
    }

    @Composable
    private fun PageIndicator(
        sm: ReaderScreenModel,
        color: Color = Color.Gray,
        modifier: Modifier = Modifier
    ) {
        Text(
            "${sm.currentPage} / ${sm.totalPages}",
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = modifier.wrapContentWidth()
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun PageNavigator(
        sm: ReaderScreenModel,
        modifier: Modifier = Modifier
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black)
                .padding(vertical = 6.dp)
                .clickable(enabled = false) {}
        ) {
            val pagerState = rememberPagerState(initialPage = sm.currentPage - 1) { sm.totalPages }
            val scope = rememberCoroutineScope()
            LaunchedEffect(pagerState.currentPage) {
                if (sm.pageNavigatorIndex != pagerState.currentPage) {
                    sm.updateFromNavigator = true
                    sm.pageNavigatorIndex = pagerState.currentPage
                }
            }
            LaunchedEffect(sm.currentPage) {
                pagerState.animateScrollToPage(sm.currentPage - 1)
            }
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(maxWidth / 5),
                contentPadding = PaddingValues(horizontal = maxWidth / 2),
                flingBehavior = PagerDefaults.flingBehavior(
                    pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(5)
                ),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                PageNavigatorIndex(
                    page = (it + 1).toString(),
                    selected = (sm.pageNavigatorIndex + 1) == it + 1
                ) {
                    scope.launch {
                        launch { pagerState.animateScrollToPage(it) }
                        sm.updateFromNavigator = true
                        sm.pageNavigatorIndex = it
                    }
                }
            }
        }
    }

    @Composable
    private fun PageNavigatorIndex(
        page: String,
        selected: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val color = if (selected) Color.White else Color.DarkGray
        val fontSize = if (selected) 16.sp else 14.sp
        Text(
            page,
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier
                .clickable(onClick = onClick)
        )
    }
}