package view

import Assets
import LocalScreenSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
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
import assets.`Chevron-right`
import assets.`Heart-outline`
import assets.`Magnifying-glass`
import assets.Person
import assets.`Text-align-right`
import assets.`Treasure-map`
import cafe.adriel.voyager.navigator.Navigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import model.Manga
import model.session.MangaSession
import model.session.Session
import model.session.SessionState
import util.APP_BAR_HEIGHT
import util.BLUR_TINT
import util.LATEST_UPDATE_SLIDE_TIME
import util.session_handler.MangaSessionHandler
import view_model.main.MainViewModel
import view_model.main.state.HomeState

private const val imageRatio = 2f / 3f
private val bottomBarTotalHeight = APP_BAR_HEIGHT + 8.dp
private val parentHorizontalPadding = 16.dp
private val tagsHorizontalPadding = 8.dp
private const val tagsRowCount = 2
private const val tagsColumnCount = 2

// TODO make its own state
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    vm: MainViewModel,
    state: HomeState,
    nav: Navigator,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val latestBarHeight = (screenSize.height.value / 4.2).dp
    val pagerState = rememberPagerState { 2 }
    val session = state.session
    val sessionState by remember {
        derivedStateOf { session.state.value }
    }
    LaunchedEffect(sessionState) {
        when(sessionState) {
            SessionState.IDLE -> pagerState.animateScrollToPage(0)
            else -> pagerState.animateScrollToPage(1)
        }
    }
    val settledPage by remember {
        derivedStateOf { pagerState.settledPage }
    }
    LaunchedEffect(settledPage) {
        if (settledPage == 0) state.clearSession()
    }
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = sessionState != SessionState.IDLE
    ) {
        when(it) {
            0 -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = parentHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Header(
                        username = state.username.value,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
                item {
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
                }
                item {
                    ContinueReading(vm = vm, state = state, nav = nav, height = latestBarHeight)
                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        val mangaTagsModifier = Modifier.fillMaxWidth()
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints.copy(
                                    maxWidth = constraints.maxWidth + 4.dp.roundToPx() * 2
                                ))
                                layout(placeable.width, placeable.height) {
                                    placeable.place(0, 0)
                                }
                            }
                            .clip(RoundedCornerShape(8.dp))
                        Text(
                            "Suggestions",
                            fontSize = 24.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                        // REMOVE LATER
//                MangaTags(
//                    mangaList = state.romCom,
//                    label = "Rom-Com",
//                    nav = nav,
//                    vm = vm,
//                    backgroundGradient = Brush.linearGradient(colors = listOf(
//                        Color(0xFFADD8E6),
//                        Color(0xFFFFB6C1)
//                    )),
//                    modifier = mangaTagsModifier
//                )
//                MangaTags(
//                    mangaList = state.advCom,
//                    label = "Adventure-Comedy",
//                    nav = nav,
//                    vm = vm,
//                    backgroundGradient = Brush.linearGradient(colors = listOf(
//                        Color(0xFFFFA07A),
//                        Color(0xFFFFC48C)
//                    )),
//                    modifier = mangaTagsModifier
//                )
//                MangaTags(
//                    mangaList = state.psyMys,
//                    label = "Psychological-Mystery",
//                    nav = nav,
//                    vm = vm,
//                    backgroundGradient = Brush.linearGradient(colors = listOf(
//                        Color(0xFF191970),
//                        Color(0xFF2F4F4F)
//                    )),
//                    unselectedDotColor = Color(0xFF949494),
//                    modifier = mangaTagsModifier
//                )
                        Tags(
                            state = state,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // to prevent contents being obstructed by bottom bar.
                item {
                    Spacer(modifier = Modifier.height(bottomBarTotalHeight - 16.dp))
                }
            }
            else -> MangaPage(
                session = session,
                onSessionLoaded = state::onSessionLoaded,
                onPainterLoaded = state::onPainterLoaded
            )
        }
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
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box {
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
        }
        Spacer(Modifier.width(8.dp))
        Text(
            username,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Assets.`Text-align-right`,
            contentDescription = "custom list",
            tint = Color.Black,
            modifier = Modifier
                .size(32.dp)
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
                        vm.navigateToDetailScreen(nav, manga)
                    }
            ) {
                val hazeState = remember { HazeState() }
                Box(
                    modifier = Modifier
                        .haze(hazeState)
                ) {
                    ImageLoader(
                        url = getCoverUrl(manga.data),
                        painter = manga.painter,
                        contentScale = ContentScale.FillWidth,
                        loading = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeChild(
                                hazeState,
                                style = HazeStyle(
                                    blurRadius = 8.dp,
                                    tint = BLUR_TINT
                                )
                            )
                    ) { p ->
                        state.latestUpdates[it] = state.latestUpdates[it].copy(painter = p)
                    }
                }
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
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color.Gray,
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
    vm: MainViewModel,
    state: HomeState,
    nav: Navigator,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val data = state.continueReading
    val screenSize = LocalScreenSize.current
    val smallDisplayHeight = height / 2
    val smallDisplayWidth = smallDisplayHeight * imageRatio + 4.dp
    if (data.isNotEmpty()) Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Continue Reading",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = Assets.`Chevron-right`,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .height(16.dp)
                    .offset(y = 1.dp)
            )
        }
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
                        state.continueReading[it] = state.continueReading[it].copy(painter = p)
                    },
                    modifier = Modifier.width(screenSize.width / 2)
                ) { vm.navigateToDetailScreen(nav, manga) }
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
    Row(modifier = modifier.clickable(onClick = onClick)) {
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
                            vm.navigateToDetailScreen(nav, manga)
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
fun tagBoxSize(): Dp = LocalScreenSize.current.width / 2 - parentHorizontalPadding - (tagsHorizontalPadding / tagsRowCount)


@Composable
private fun Tags(
    state: HomeState,
    modifier: Modifier = Modifier
) {
    val tagBoxSize = tagBoxSize()
    SubcomposeLayout(modifier = modifier) { constraints ->
        val verticalArrangement = 8.dp
        val measurable = subcompose("grid") {
            LazyHorizontalGrid(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(verticalArrangement),
                rows = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { RomComTag(state) }
                item { PsyMysTag(state) }
                item { AdvComTag(state) }
            }
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
fun PsyMysTag(
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
fun MangaPage(
    session: MangaSession,
    onSessionLoaded: (Session<Manga, MangaAttributes>) -> Unit,
    onPainterLoaded: (Int, Painter) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val state = rememberLazyListState()
    if (session.state.value == SessionState.ACTIVE) LaunchedEffect(true) { state.scrollToItem(0) }
    Box(modifier = modifier.fillMaxSize()) {
        when(session.state.value) {
            SessionState.FETCHING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                        )
                    }
                }
            }
            else -> {
                // TODO change to SessionPagerVerticalPager
                SessionPagerColumn(
                    session = session,
                    state = state,
                    handler = MangaSessionHandler(session),
                    onSessionLoaded = onSessionLoaded,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = APP_BAR_HEIGHT + 8.dp)
                ) {
                    val manga = session.data[it]
                    Row(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        ImageLoader(
                            url = getCoverUrl(manga.data),
                            painter = manga.painter,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxHeight().weight(0.3f)
                        ) { p -> onPainterLoaded(it, p) }
                        Text(
                            getTitle(manga.data.attributes.title),
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }
            }
        }
    }
}