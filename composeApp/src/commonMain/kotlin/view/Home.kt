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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTags
import api.mangadex.util.getTitle
import assets.`Chevron-right`
import assets.Person
import assets.`Text-align-right`
import cafe.adriel.voyager.navigator.Navigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import model.Manga
import util.APP_BAR_HEIGHT
import util.BLUR_TINT
import util.LATEST_UPDATE_SLIDE_TIME
import view_model.main.MainViewModel

private const val imageRatio = 2f / 3f
private val bottomBarTotalHeight = APP_BAR_HEIGHT + 8.dp

// TODO make its own state
@Composable
fun Home(
    vm: MainViewModel,
    nav: Navigator,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val latestBarHeight = (screenSize.height.value / 4.2).dp
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Header(
                username = vm.username.value,
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
                    height = latestBarHeight,
                    nav = nav,
                )
            }
        }
        item {
            ContinueReading(vm = vm, nav = nav, height = latestBarHeight)
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
                MangaTags(
                    mangaList = vm.romCom,
                    label = "Rom-Com",
                    nav = nav,
                    vm = vm,
                    backgroundGradient = Brush.linearGradient(colors = listOf(
                        Color(0xFFADD8E6),
                        Color(0xFFFFB6C1)
                    )),
                    modifier = mangaTagsModifier
                )
                MangaTags(
                    mangaList = vm.advCom,
                    label = "Adventure-Comedy",
                    nav = nav,
                    vm = vm,
                    backgroundGradient = Brush.linearGradient(colors = listOf(
                        Color(0xFFFFA07A),
                        Color(0xFFFFC48C)
                    )),
                    modifier = mangaTagsModifier
                )
                MangaTags(
                    mangaList = vm.psyMys,
                    label = "Psychological-Mystery",
                    nav = nav,
                    vm = vm,
                    backgroundGradient = Brush.linearGradient(colors = listOf(
                        Color(0xFF191970),
                        Color(0xFF2F4F4F)
                    )),
                    unselectedDotColor = Color(0xFF949494),
                    modifier = mangaTagsModifier
                )
            }
        }
        // to prevent contents being obstructed by bottom bar.
        item {
            Spacer(modifier = Modifier.height(bottomBarTotalHeight - 16.dp))
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
                    animationSpec = tween(1000)
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

// TODO(find a way to adjust the auto-slide bar and limit the scope of
//  composables that read the pager state as small as possible)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LatestUpdatesBar(
    vm: MainViewModel,
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
    ) {
        val data = vm.latestUpdates
        val pagerState = rememberPagerState(initialPage = vm.latestUpdatesBarPage) { vm.sessionSize }
        autoSlideLatestUpdates(
            autoSlide = vm.enableAutoSlide.value,
            pagerState = pagerState
        )
        val adjustBar by remember {
            derivedStateOf {
                pagerState.currentPageOffsetFraction != 0.0f
            }
        }
        LaunchedEffect(adjustBar) {
            vm.adjustLatestUpdatesBar = adjustBar
        }
        LaunchedEffect(true) {
            if (vm.adjustLatestUpdatesBar) pagerState.animateScrollToPage(
                pagerState.currentPage,
                animationSpec = tween(500)
            )
        }
        val currentPage by remember {
            derivedStateOf { pagerState.currentPage }
        }
        LaunchedEffect(currentPage) {
            vm.latestUpdatesBarPage = currentPage
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
                        vm.navigateToDetailScreen(nav, manga.painter, manga)
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
                        vm.latestUpdates[it] = vm.latestUpdates[it].copy(painter = p)
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
            n = pagerState.pageCount,
            selected = pagerState.currentPage + 1,
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
    nav: Navigator,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val data = vm.continueReading
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
                        vm.continueReading[it] = vm.continueReading[it].copy(painter = p)
                    },
                    modifier = Modifier.width(screenSize.width / 2)
                ) { vm.navigateToDetailScreen(nav, manga.painter, manga) }
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
                            vm.navigateToDetailScreen(nav, p, manga)
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