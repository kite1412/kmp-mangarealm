package view

import Assets
import LocalScreenSize
import LocalWidthClass
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTags
import api.mangadex.util.getTitle
import api.mangadex.util.obstruct
import assets.Atom
import assets.`Book-bookmark`
import assets.`Bxs-book-bookmark`
import assets.`Heart-outline`
import assets.List
import assets.`Magic-wand`
import assets.`Magnifying-glass`
import assets.Person
import assets.Settings
import assets.`Text-align-right`
import assets.`Treasure-map`
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.WidthClass
import model.session.MangaSession
import model.session.Session
import model.session.SessionState
import theme.lightBeige
import util.LATEST_UPDATE_SLIDE_TIME
import util.edgeToEdge
import util.getMaxDimension
import util.getMinDimension
import util.session_handler.MangaSessionHandler
import util.swipeToPop
import util.undoEdgeToEdge
import view_model.main.MainViewModel
import view_model.main.bottomBarTotalHeight
import view_model.main.state.HomeState
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val imageRatio = 2f / 3f
private val parentHorizontalPadding = 16.dp
private val tagsHorizontalPadding = 8.dp
@Composable
fun tagsRowCount(): Int = when(LocalWidthClass.current) {
    WidthClass.Compact -> 2
    WidthClass.Medium -> 3
    else -> 4
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    vm: MainViewModel,
    nav: Navigator,
    modifier: Modifier = Modifier
) {
    val latestBarHeight = getMaxDimension() / 4f
    val state = vm.homeState
    val session = vm.homeState.session
    val scope = rememberCoroutineScope()
    val showSuggestion = state.showSuggestion
    if (vm.undoEdgeToEdge && !showSuggestion) undoEdgeToEdge()
    val mangaPagerState = rememberPagerState { session.data.size }
    val scrollState = rememberScrollState()
    AnimatedContent(
        targetState = showSuggestion,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInHorizontally {
                    if (showSuggestion) it else -it
                } + fadeIn(),
                initialContentExit = slideOutHorizontally {
                    if (showSuggestion) -it else it
                } + fadeOut()
            )
        }
    ) {
        if (!it) {
            var headerHeight by remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = parentHorizontalPadding)
            ) {
                Header(
                    username = state.username.value,
                    state = state,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .onGloballyPositioned { coordinates ->
                            with(density) {
                                headerHeight = coordinates.size.height.toDp()
                            }
                        }
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(top = headerHeight + 16.dp + 24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Latest Updates",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        LatestUpdatesBar(
                            vm = vm,
                            state = state,
                            height = latestBarHeight,
                            nav = nav,
                        )
                    }
                    ContinueReading(
                        state = state,
                        nav = nav,
                        height = latestBarHeight
                    ) { m -> vm.navigateToDetail(nav, m) }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            "Suggestions",
                            fontSize = 24.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                        Suggestions(
                            state = state,
                            modifier = Modifier.fillMaxWidth(),
                            items = listOf(
                                { RomComTag(state) },
                                { AdvComTag(state) },
                                { PsyMysTag(state) },
                                { IsekaiTag(state) },
                                { SciFiTag(state) },
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(bottomBarTotalHeight - 8.dp))
                }
                AnimatedVisibility(
                    visible = state.showOptions,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = headerHeight + 16.dp)
                ) {
                    Options(
                        onMyListClick = { state.navigateToCustomListScreen(nav) },
                        onSettingsClick = { state.navigateToSettingsScreen(nav) }
                    )
                }
            }
        } else MangaPage(
            session = state.session,
            onSessionLoaded = state::onSessionLoaded,
            pop = {
                scope.launch {
                    state.clearSession()
                    vm.hideNavigationBar = false
                    mangaPagerState.scrollToPage(0)
                    state.showSuggestion = false
                }
            },
            onChapterListClick = { m -> vm.navigateToChapter(nav, m) },
            onStatusUpdate = state::onStatusUpdate,
            pagerState = mangaPagerState,
            onPainterLoaded = state::onPainterLoaded
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun autoSlideLatestUpdates(
    autoSlide: Boolean,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(pagerState.settledPage, autoSlide) {
        while (autoSlide) {
            delay(LATEST_UPDATE_SLIDE_TIME.toLong())
            if (pagerState.currentPage == pagerState.pageCount - 1) {
                pagerState.animateScrollToPage(
                    0,
                    pageOffsetFraction = 0f,
                    animationSpec = tween(500)
                )
            } else {
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    pageOffsetFraction = 0f,
                    animationSpec = tween(500)
                )
            }
        }
    }
}

@Composable
private fun Header(
    username: String,
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    Assets.Person,
                    contentDescription = "person icon",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                username,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Assets.`Text-align-right`,
            contentDescription = "options",
            tint = MaterialTheme.typography.body1.color,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(32.dp)
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ) { state.onOptionsClick() }
        )
    }
}

@Composable
private fun Options(
    onMyListClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(enabled = false) {}
    ) {
        Option(
            action = "My List",
            icon = Assets.List,
            modifier = Modifier.fillMaxWidth(),
            onClick = onMyListClick
        )
        Option(
            action = "Settings",
            icon = Assets.Settings,
            modifier = Modifier.fillMaxWidth(),
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun Option(
    action: String,
    icon: ImageVector,
    color: Color = Color.Black,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = action,
            tint = color,
        )
        Text(
            action,
            color = color,
            fontSize = 16.sp,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LatestUpdatesBar(
    vm: MainViewModel,
    state: HomeState,
    height: Dp,
    nav: Navigator,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .background(Color.Transparent)
    ) {
        val data = state.latestUpdates
        val pagerState = rememberPagerState(initialPage = state.latestUpdatesBarPage) { state.sessionSize }
        autoSlideLatestUpdates(
            autoSlide = state.enableAutoSlide.value,
            pagerState = pagerState
        )
        LaunchedEffect(true) {
            pagerState.animateScrollToPage(
                state.latestUpdatesBarPage,
                animationSpec = tween(300)
            )
        }
        val currentPage by remember {
            derivedStateOf { pagerState.currentPage }
        }
        LaunchedEffect(currentPage) {
            state.latestUpdatesBarPage = currentPage
        }
        if (data.isNotEmpty()) HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
            val imageHeight = height / 1.5f
            val manga = data[it]
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .clickable {
                        vm.navigateToDetail(nav, manga)
                    }
            ) {
                ImageLoader(
                    url = getCoverUrl(manga.data),
                    painter = manga.painter,
                    contentScale = ContentScale.FillWidth,
                    loading = {},
                    // TODO change later
                    visibilityThresholdSize = false,
                    modifier = Modifier.fillMaxSize()
                ) { p ->
                    state.latestUpdates[it] = state.latestUpdates[it].copy(painter = p)
                }
                Box(modifier = Modifier.obstruct())
                LatestUpdateDisplay(
                    manga = manga,
                    imageHeight = imageHeight,
                    imageWidth = (imageHeight * imageRatio),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            end = 12.dp
                        )
                )
            }
        } else Box(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White.copy(alpha = 0.4f))
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.background,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        IndicatorDots(
            n = state.sessionSize,
            selected = currentPage + 1,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun IndicatorDots(
    n: Int,
    selected: Int = 1,
    dotSize: Dp = 10.dp,
    spacing: Dp = 2.dp,
    selectedColor: Color = Color(255, 192, 0),
    unselectedColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (i in 1..n) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color = if (i == selected) selectedColor else unselectedColor)
            )
        }
    }
}

@Composable
private fun LatestUpdateDisplay(
    manga: Manga,
    imageHeight: Dp,
    imageWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val screenSize = LocalScreenSize.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(end = 8.dp)
                .width(screenSize.width / 1.7f)
        ) {
            val attributes = manga.data.attributes
            Text(
                getTitle(attributes.title),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.End,
                lineHeight = 16.sp
            )
            Text(
                getDesc(attributes.description),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 10.sp
            )
        }
        BrowseImageNullable(
            painter = manga.painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(imageHeight)
                .width(imageWidth)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}

@Composable
private fun ContinueReading(
    state: HomeState,
    nav: Navigator,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: (Manga) -> Unit
) {
    val data = state.continueReading
    val screenSize = LocalScreenSize.current
    val smallDisplayHeight = height / 2
    val smallDisplayWidth = smallDisplayHeight * imageRatio + 4.dp
    if (data.isNotEmpty()) Column(modifier = modifier) {
        Text(
            "Continue Reading",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        LazyHorizontalGrid(
            rows = GridCells.Fixed(if (data.size > 1) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(if (data.size > 1) height else smallDisplayHeight)
                .padding(start = 4.dp)
        ) {
            items(count = data.size) {
                val manga = data[it]
                SmallDisplay(
                    manga = manga,
                    imageWidth = smallDisplayWidth,
                    imageHeight = smallDisplayHeight,
                    onPainterLoaded = { p ->
                        state.sharedViewModel.onMangaStatusPainterLoaded(
                            status = MangaStatus.Reading,
                            painter = p,
                            index = it
                        )
                    },
                    modifier = Modifier.width(screenSize.width / 2)
                ) { onClick(manga) }
            }
        }
    }
}

@Composable
private fun SmallDisplay(
    manga: Manga,
    imageWidth: Dp,
    imageHeight: Dp,
    onPainterLoaded: (Painter) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val data = manga.data
    Row(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource(),
                onClick = onClick
            )
    ) {
        ImageLoader(
            url = getCoverUrl(manga.data),
            painter = manga.painter,
            onPainterLoaded = onPainterLoaded,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(imageWidth)
                .height(imageHeight)
                .clip(RoundedCornerShape(5.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            getTitle(data.attributes.title),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MangaTags (
    mangaList: SnapshotStateList<Manga>,
    label: String,
    nav: Navigator,
    backgroundGradient: Brush,
    vm: MainViewModel,
    unselectedDotColor: Color = Color(0xFFDEDEDE),
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val height = screenSize.height / 3.5f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(brush = backgroundGradient)
            .padding(top = 16.dp, start = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var currentPage by remember { mutableStateOf(0) }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Text(
                    label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IndicatorDots(
                    n = mangaList.size,
                    selected = currentPage + 1,
                    dotSize = 6.dp,
                    spacing = 1.dp,
                    unselectedColor = unselectedDotColor
                )
            }
            if (mangaList.isNotEmpty()) BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
            ) {
                val pagerState = rememberPagerState { mangaList.size }
                val pagerCurrentPage by remember {
                    derivedStateOf { pagerState.currentPage }
                }
                LaunchedEffect(pagerCurrentPage) { currentPage = pagerCurrentPage }
                val tagsDisplayWidth = maxWidth / 1.2f
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fixed(tagsDisplayWidth),
                    pageSpacing = 16.dp,
                    verticalAlignment = Alignment.Bottom,
                    contentPadding = PaddingValues(end = maxWidth - tagsDisplayWidth),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val manga = mangaList[it]
                    TagsDisplay(
                        manga = manga,
                        painter = manga.painter,
                        parentHeight = maxHeight,
                        isSelected = pagerState.currentPage == it,
                        onClick = { p ->
                            vm.navigateToDetail(nav, manga)
                        },
                        onPainterLoaded = { p -> mangaList[it] = mangaList[it].copy(painter = p) },
                        modifier = Modifier.wrapContentHeight()
                    )
                }
            } else Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun TagsDisplay(
    manga: Manga,
    painter: Painter?,
    parentHeight: Dp,
    isSelected: Boolean,
    onClick: (Painter?) -> Unit,
    onPainterLoaded: (Painter) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageHeight by animateDpAsState(if (isSelected) parentHeight else parentHeight - (parentHeight / 8))
    val imageWidth = imageHeight * imageRatio
    val obstructColor by animateColorAsState(if (!isSelected) Color.Black else Color.Transparent)
    BoxWithConstraints(
        modifier = modifier
            .height(imageHeight)
            .fillMaxWidth()
            .clickable {
                onClick(painter)
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(imageWidth)
                    .height(imageHeight)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White)
                    .align(Alignment.Bottom)
            ) {
                ImageLoader(
                    url = getCoverUrl(manga.data),
                    painter = painter,
                    contentDescription = "cover art",
                    contentScale = ContentScale.FillBounds,
                    onPainterLoaded = onPainterLoaded,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = Brush.linearGradient(listOf(
                            obstructColor, Color.Transparent
                        )))
                ) {}
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val data = manga.data
                Text(
                    getTitle(data.attributes.title),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    getTags(data),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    getDesc(data.attributes.description),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun tagBoxSize(): Dp {
    val rowCount = tagsRowCount()
    return LocalScreenSize.current.width / rowCount - parentHorizontalPadding - (tagsHorizontalPadding / rowCount)
}


@Composable
private fun Suggestions(
    state: HomeState,
    modifier: Modifier = Modifier,
    items: List<@Composable () -> Unit>,
) {
    val tagBoxSize = tagBoxSize()
    val widthClass = LocalWidthClass.current
    SubcomposeLayout(modifier = modifier) { constraints ->
        val verticalArrangement = 8.dp
        val row = when(widthClass) {
            WidthClass.Compact -> 2f
            WidthClass.Medium -> 3f
            else -> 4f
        }
        val tagsColumnCount = ceil((items.size / row)).roundToInt()
        val measurable = subcompose("grid") {
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(verticalArrangement),
                columns = GridCells.Fixed(row.toInt()),
                userScrollEnabled = false,
                modifier = Modifier.fillMaxWidth()
            ) { items.forEach { item { it() } } }
        }
        val placeable = measurable.map {
            it.measure(
                constraints.copy(
                    maxHeight = ((tagBoxSize * tagsColumnCount + ((tagsColumnCount - 1) * verticalArrangement))).roundToPx()
                )
            )
        }
        val totalHeight = placeable.sumOf { it.height }
        layout(constraints.maxWidth, totalHeight) {
            var nextHeight = 0
            placeable.forEach {
                it.placeRelative(0, nextHeight)
                nextHeight += (it.height + verticalArrangement.roundToPx())
            }
        }
    }
}

@Composable
private fun Tag(
    tag: String,
    backgroundGradient: Pair<Color, Color>,
    onClick: () -> Unit,
    fontSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(Dp) -> Unit,
) {
    val size = tagBoxSize()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .size(size)
            .background(
                brush = Brush.linearGradient(
                    listOf(backgroundGradient.first, backgroundGradient.second)
                )
            )
            .clickable(onClick = onClick)
        ,
    ) {
        content(size)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                tag,
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Clip,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun RomComTag(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Tag(
        tag = "Rom-Com",
        backgroundGradient = Color(0xFFADD8E6) to Color(0xFFFFB6C1),
        onClick = {
            state.beginSession(
                queries = state.setIncludedExcludedTags(
                    tags = state.romComTags.first,
                    excludedTags = state.romComTags.second
                )
            )
        }
    ) {
        Icon(
            imageVector = Assets.`Heart-outline`,
            contentDescription = "rom-com",
            tint = Color(0xFFEEA8B2),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(it / 1.2f)
                .rotate(-30f)
                .offset(y = it / 4f)
        )
    }
}

@Composable
private fun AdvComTag(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Tag(
        tag = "Adventure Comedy",
        backgroundGradient = Color(0xFFFFA07A) to Color(0xFFFFC48C),
        onClick = {
            state.beginSession(
                queries = state.setIncludedExcludedTags(
                    tags = state.advComTags.first,
                    excludedTags = state.advComTags.second
                )
            )
        }
    ) {
        Icon(
            imageVector = Assets.`Treasure-map`,
            contentDescription = "adventure comedy",
            tint = Color(0xFFFFAA6E),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = it / 4f, x = it / 10f)
                .size(it / 1.5f)
                .rotate(30f)
        )
    }
}

@Composable
private fun PsyMysTag(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Tag(
        tag = "Psychological Mystery",
        backgroundGradient = Color(0xFF191970) to Color(0xFF2F4F4F),
        onClick = {
            state.beginSession(
                queries = state.setIncludedExcludedTags(
                    tags = state.psyMysTags.first,
                    excludedTags = state.psyMysTags.second
                )
            )
        },
        fontSize = 22.sp
    ) {
        Icon(
            imageVector = Assets.`Magnifying-glass`,
            contentDescription = "psychological mystery",
            tint = Color(0xFF373785),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = -(it / 11f), y = it / 10f)
                .size(it / 1.5f)
        )
    }
}

@Composable
private fun IsekaiTag(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Tag(
        tag = "Isekai",
        backgroundGradient = Color(0xFF4B0082) to Color(0xFF87CEEB),
        onClick = {
            state.beginSession(
                queries = state.setIncludedExcludedTags(
                    tags = state.iseTags.first,
                    excludedTags = state.iseTags.second
                )
            )
        }
    ) {
        Icon(
            imageVector = Assets.`Magic-wand`,
            contentDescription = "isekai",
            tint = Color(0xFF6A5ACD),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .scale(scaleX = -1f, scaleY = 1f)
                .offset(y = it / 4f)
                .size(it / 1.5f)
        )
    }
}

@Composable
private fun SciFiTag(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    Tag(
        tag = "Sci-Fi",
        backgroundGradient = Color(0xFFFF6347) to Color(0xFF4682B4),
        onClick = {
            state.beginSession(
                queries = state.setIncludedExcludedTags(
                    tags = state.sciTags.first,
                    excludedTags = state.sciTags.second
                )
            )
        }
    ) {
        Icon(
            imageVector = Assets.Atom,
            contentDescription = "sci-fi",
            tint = Color(0xFFCC4C3C),
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(it / 1.7f)
                .rotate(-30f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, InternalVoyagerApi::class)
@Composable
private fun MangaPage(
    session: MangaSession,
    onSessionLoaded: (Session<Manga, MangaAttributes>) -> Unit,
    pop: () -> Unit,
    onChapterListClick: (Manga) -> Unit,
    onStatusUpdate: (Boolean, Int) -> Unit,
    pagerState: PagerState,
    onPainterLoaded: (Int, Painter) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    edgeToEdge()
    val sessionState by session.state
    BackHandler(
        enabled = sessionState != SessionState.FETCHING,
        onBack = pop
    )
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .consumeWindowInsets(WindowInsets.navigationBars)
            .background(lightBeige)
            .swipeToPop(action = pop, enabled = sessionState != SessionState.FETCHING)
    ) {
        when(sessionState) {
            SessionState.FETCHING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                ) {
                    LoadingIndicator(Modifier.align(Alignment.Center))
                }
            }
            SessionState.ACTIVE -> {
                var chapterListBotPadding by remember { mutableStateOf(0.dp) }
                val scope = rememberCoroutineScope()
                SessionPagerVerticalPager(
                    session = session,
                    state = pagerState,
                    handler = MangaSessionHandler(session),
                    pageSize = PageSize.Fixed(maxHeight - (chapterListBotPadding / 2)),
                    onSessionLoaded = onSessionLoaded,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val manga = session.data[it]
                    MangaPageDisplay(
                        manga = manga,
                        onChapterListClick = { onChapterListClick(manga) },
                        onStatusUpdate = { r -> onStatusUpdate(r, it) },
                        onOverscrollDesc = { forward ->
                            scope.launch {
                                if (forward) {
                                    val next = it + 1
                                    if ((session.data.size - 1) > next) pagerState.animateScrollToPage(next)
                                } else if (it > 0) pagerState.animateScrollToPage(it - 1)
                            }
                        },
                        onPainterLoaded = { p -> onPainterLoaded(it, p) },
                        chapterHeight = { h -> chapterListBotPadding = h }
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun MangaPageDisplay(
    manga: Manga,
    onChapterListClick: (Manga) -> Unit,
    chapterHeight: (Dp) -> Unit,
    onOverscrollDesc: (forward: Boolean) -> Unit,
    onStatusUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onPainterLoaded: (Painter) -> Unit
) {
    val descState = rememberScrollState()
    LaunchedEffect(true) {
        descState.scrollBy(0f)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        val minDimension = getMinDimension()
        ImageLoader(
            url = getCoverUrl(manga.data),
            painter = manga.painter,
            contentScale = ContentScale.FillBounds,
            loading = {},
            modifier = Modifier.fillMaxSize(),
            onPainterLoaded = onPainterLoaded
        )
        Box(Modifier.obstruct(color = Color.Black.copy(alpha = 0.8f)))
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = statusBarHeight + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            var chapterListHeight by remember { mutableStateOf(0.dp) }
            val coverWidth = minDimension / 2.2f
            val coverHeight = coverWidth * (3f / 2f)
            val screenHeight = LocalScreenSize.current.height
            val rowInfo = coverHeight >= screenHeight / 2
            if (manga.painter != null) Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = chapterListHeight)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    var read by remember { mutableStateOf(manga.status == MangaStatus.Reading) }
                    val icon  = if (read) Assets.`Bxs-book-bookmark` else Assets.`Book-bookmark`
                    if (rowInfo) MangaPageInfo(
                        manga = manga,
                        descState = descState,
                        onOverscrollDesc = onOverscrollDesc,
                        modifier = Modifier.weight(0.5f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedContent(
                            targetState = read,
                            modifier = Modifier.align(Alignment.Bottom)
                        ) {
                            Action(
                                onClick = {
                                    read = !read
                                    onStatusUpdate(read)
                                },
                                fill = !read,
                                verticalPadding = 16.dp,
                                horizontalPadding = 16.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (it) Text(
                                        "Reading",
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colors.secondary,
                                    )
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = if (it) "remove from reading list" else "add to reading list",
                                        tint = if (it) MaterialTheme.colors.secondary else Color.White
                                    )
                                }
                            }
                        }
                        BrowseImageNullable(
                            painter = manga.painter,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(coverWidth)
                                .height(coverHeight)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
                if (!rowInfo) MangaPageInfo(
                    manga = manga,
                    descState = descState,
                    onOverscrollDesc = onOverscrollDesc
                )
            } else LoadingIndicator(Modifier.align(Alignment.Center))
            val density = LocalDensity.current
            if (manga.painter != null) Action(
                onClick =  { onChapterListClick(manga) },
                verticalPadding = 16.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(minDimension / 2)
                    .padding(bottom = 8.dp)
                    .onGloballyPositioned {
                        with(density) {
                            chapterListHeight = (it.size.height.toDp() + 16.dp)
                            chapterHeight(chapterListHeight)
                        }
                    }
            ) {
                Text(
                    "Chapter List",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun MangaPageInfo(
    manga: Manga,
    descState: ScrollState,
    onOverscrollDesc: (forward: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier  = modifier
    ) {
        Title(
            manga = manga.data.attributes,
            textAlign = TextAlign.Center
        )
        Tags(
            data = manga.data.attributes,
            horizontalAlignment = Alignment.CenterHorizontally,
            fontColor = Color.White,
            fontSize = 12.sp,
            horizontalPadding = 4.dp,
            cornerRadius = 6.dp,
            modifier = Modifier.fillMaxWidth()
        )
        val scope = rememberCoroutineScope()
        Text(
            getDesc(manga.data.attributes.description),
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .verticalScroll(descState, false)
                .pointerInput(true) {
                    detectVerticalDragGestures { _, dragAmount ->
                        var forward = false
                        val scrollText = if (dragAmount > 0.0f)
                            if (dragAmount >= 40) false else true
                        else if (dragAmount <= -40) false.also {
                            forward = true
                        } else true
                        scope.launch {
                            if (scrollText) {
                                descState.scrollBy(-(dragAmount))
                            } else {
                                onOverscrollDesc(forward)
                                descState.scrollTo(0)
                            }
                        }
                    }
                }
        )
    }
}