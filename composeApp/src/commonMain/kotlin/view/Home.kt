package view

import Assets
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
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
import screenSize
import util.APP_BAR_HEIGHT
import util.BLUR_TINT
import util.LATEST_UPDATE_SLIDE_TIME
import viewmodel.MainViewModel

private const val imageRatio = 2f / 3f
private val latestBarHeight = (screenSize.height.value / 4.2).dp
private val bottomBarTotalHeight = APP_BAR_HEIGHT + 8.dp
private val smallDisplayHeight = latestBarHeight / 2
private val smallDisplayWidth = smallDisplayHeight * imageRatio + 4.dp
private val tagsDisplayWidth = (screenSize.width / 1.3f)
private val mangaTagsModifier = Modifier.fillMaxWidth()
    .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(
            maxWidth = constraints.maxWidth + 4.dp.roundToPx() * 2
        ))
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
    .clip(RoundedCornerShape(8.dp))

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    vm: MainViewModel,
    nav: Navigator,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = vm.latestUpdatesBarPage) { vm.sessionSize }
    autoSlideLatestUpdates(
        autoSlide = vm.enableAutoSlide.value,
        pagerState = pagerState
    )
    LaunchedEffect(pagerState.currentPageOffsetFraction) {
        vm.adjustLatestUpdatesBar = pagerState.currentPageOffsetFraction != 0.0f
    }
    LaunchedEffect(true) {
        if (vm.adjustLatestUpdatesBar) pagerState.animateScrollToPage(
            pagerState.currentPage,
            animationSpec = tween(500)
        )
    }
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
                    pagerState = pagerState,
                )
            }
        }
        item {
            ContinueReading(vm = vm, nav = nav)
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "Suggestions",
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
                MangaTags(
                    mangaList = vm.romComManga,
                    painters = vm.romComPainter,
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
                    mangaList = vm.advComManga,
                    painters = vm.advComPainter,
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
                    mangaList = vm.psyMysManga,
                    painters = vm.psyMysPainter,
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
            Spacer(modifier = androidx.compose.ui.Modifier.height(bottomBarTotalHeight - 16.dp))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LatestUpdatesBar(
    vm: MainViewModel,
    height: Dp,
    nav: Navigator,
    pagerState: PagerState = rememberPagerState(initialPage = vm.latestUpdatesBarPage) { vm.sessionSize },
    modifier: Modifier = Modifier
) {
    LaunchedEffect(pagerState.currentPage) {
        vm.latestUpdatesBarPage = pagerState.currentPage
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
    ) {
        if (vm.latestUpdatesData.isNotEmpty() && vm.latestUpdatesPainter.isNotEmpty()) HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
            val imageHeight = height / 1.5f
            val painter = vm.latestUpdatesPainter[it]
            val data = vm.latestUpdatesData[it]
            if (painter == null) PainterLoader(
                url = getCoverUrl(data),
            ) { p -> vm.latestUpdatesPainter[it] = p }
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .clickable {
                        vm.navigateToDetailScreen(nav, painter, Manga(data))
                    }
            ) {
                val hazeState = remember { HazeState() }
                Box(
                    modifier = Modifier
                        .haze(hazeState)
                ) {
                    BrowseImageNullable(
                        painter = painter,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeChild(
                                hazeState,
                                style = HazeStyle(
                                    blurRadius = 8.dp,
                                    tint = BLUR_TINT
                                )
                            )
                    ) {}
                }
                LatestUpdateDisplay(
                    painter = painter,
                    title = getTitle(data.attributes.title),
                    desc = getDesc(data.attributes.description),
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
    painter: Painter?,
    title: String,
    desc: String,
    imageHeight: Dp,
    imageWidth: Dp,
    modifier: Modifier = Modifier,
) {
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
            Text(
                title,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.End,
                lineHeight = 16.sp
            )
            Text(
                desc,
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
            painter = painter,
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
    modifier: Modifier = Modifier
) {
    if (vm.continueReadingData.isNotEmpty() && vm.continueReadingPainter.isNotEmpty()) Column(modifier = modifier) {
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
            rows = GridCells.Fixed(if (vm.continueReadingData.size > 1) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(if (vm.continueReadingData.size > 1) latestBarHeight else smallDisplayHeight)
                .padding(start = 4.dp)
        ) {
            items(count = vm.continueReadingData.size) {
                val painter = vm.continueReadingPainter[it]
                val data = vm.continueReadingData[it]
                if (painter == null) PainterLoader(
                    url = getCoverUrl(data)
                ) { p -> vm.continueReadingPainter[it] = p }
                SmallDisplay(
                    painter = painter,
                    title = getTitle(data.attributes.title),
                    modifier = Modifier.width(screenSize.width / 2)
                ) { vm.navigateToDetailScreen(nav, painter, Manga(data)) }
            }
        }
    }
}

@Composable
private fun SmallDisplay(
    painter: Painter?,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = modifier.clickable(onClick = onClick)) {
        BrowseImageNullable(
            painter = painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(smallDisplayWidth)
                .height(smallDisplayHeight)
                .clip(RoundedCornerShape(5.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            title,
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
    mangaList: List<Data<MangaAttributes>>,
    painters: List<Painter?>,
    label: String,
    nav: Navigator,
    backgroundGradient: Brush,
    vm: MainViewModel,
    unselectedDotColor: Color = Color(0xFFDEDEDE),
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { mangaList.size }
    val labelHeight  = vm.mangaTagsLabelHeight.value.dp
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(labelHeight + (tagsDisplayWidth * imageRatio))
            .background(brush = backgroundGradient)
            .padding(top = 16.dp, start = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                val density = LocalDensity.current
                Text(
                    label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.onGloballyPositioned {
                        with(density) {
                            vm.mangaTagsLabelHeight.value = it.size.height.toDp().value.toInt()
                        }
                    }
                )
                IndicatorDots(
                    n = pagerState.pageCount,
                    selected = (pagerState.currentPage + 1),
                    dotSize = 6.dp,
                    spacing = 1.dp,
                    unselectedColor = unselectedDotColor
                )
            }
            if (mangaList.isNotEmpty() && painters.isNotEmpty()) HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(bottom = 16.dp),
                pageSize = PageSize.Fixed(tagsDisplayWidth),
                pageSpacing = 16.dp,
                verticalAlignment = Alignment.Bottom
            ) {
                val painter = painters[it]
                TagsDisplay(
                    manga = mangaList[it],
                    painter = painter,
                    contentWidth = tagsDisplayWidth,
                    isSelected = it == pagerState.currentPage,
                    onClick = { p ->
                        vm.navigateToDetailScreen(nav, painter, Manga(mangaList[it]))
                    },
                    modifier = Modifier.offset(x = if (pagerState.currentPage == mangaList.size - 1)
                        -((screenSize.width - tagsDisplayWidth)
                                - 16.dp // page spacing
                                - 8.dp // additional width (right and left)
                                - 16.dp // parent padding
                                )
                    else 0.dp)
                )
            } else Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun TagsDisplay(
    manga: Data<MangaAttributes>,
    painter: Painter?,
    contentWidth: Dp,
    isSelected: Boolean,
    onClick: (Painter?) -> Unit,
    modifier: Modifier = Modifier
) {
    val width = contentWidth - (contentWidth / 8)
    val imageWidth by animateDpAsState(if (isSelected) width / 2.4f else width / 2.8f)
    val imageHeight = (imageWidth * 3) / 2
    val height = (width * 3) / 2
    val obstructColor by animateColorAsState(if (!isSelected) Color.Black else Color.Transparent)
    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .width(width)
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
                BrowseImageNullable(
                    painter = painter,
                    contentDescription = "cover art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
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
                Text(
                    getTitle(manga.attributes.title),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    getTags(manga),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    getDesc(manga.attributes.description),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}