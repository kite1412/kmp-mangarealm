package view

import Assets
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import assets.`Book-close`
import assets.`Chevron-right-bold`
import assets.Clipboard
import assets.Home
import assets.Person
import assets.Search
import assets.`Text-align-right`
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import screenSize
import theme.selectedButton
import theme.unselectedButton
import util.LATEST_UPDATE_SLIDE_TIME
import viewmodel.MainViewModel
import viewmodel.Page

private const val imageRatio = 2f / 3f
private val latestBarHeight = (screenSize.height.value / 4.2).dp
private val bottomBarHeight = 62.dp
private val bottomBarTotalHeight = bottomBarHeight + 8.dp
private val smallDisplayHeight = latestBarHeight / 2
private val smallDisplayWidth = smallDisplayHeight * imageRatio + 4.dp

@Composable
fun MainScreen(vm: MainViewModel = MainViewModel()) {
    vm.init()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Header(
                        "kite1412",
                        modifier = Modifier.padding(top = 24.dp,)
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
                        )
                    }
                }
                item {
                    ContinueReading(vm = vm)
                }
                item {
                    MangaGenre(
                        label = "Rom-Coms",
                        backgroundGradient = Pair(Color(0xFFFC00C4), Color(0xFFC232C5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints.copy(
                                    maxWidth = constraints.maxWidth + 16.dp.roundToPx() * 2
                                ))
                                layout(placeable.width, placeable.height) {
                                    placeable.place(0, 0)
                                }
                            }
                    )
                }
                // to prevent contents being obstructed by bottom bar.
                item {
                    Spacer(modifier = Modifier.height(bottomBarTotalHeight - 16.dp))
                }
            }
            BottomAppBar(
                page = vm.currentPage.value,
                onPageChange = {
                    vm.currentPage.value = it
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }
    val pagerState = rememberPagerState { 10 }
    var isOnManualSwipe by remember { mutableStateOf(false) }
    LaunchedEffect(isOnManualSwipe) {
        while (!isOnManualSwipe) {
            delay(vm.latestUpdateSlideTime.value.toLong())
            vm.adjustLatestUpdatesSlideTime(LATEST_UPDATE_SLIDE_TIME)
            if (pagerState.currentPage == pagerState.pageCount - 1) {
                pagerState.animateScrollToPage(
                    0,
                    animationSpec = tween(500)
                )
            } else {
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec = tween(1000)
                )
            }
        }
    }
    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.isScrollInProgress) {
            isOnManualSwipe = true
            delay(500)
        }
        isOnManualSwipe = false
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val imageHeight = height / 1.5f
            HorizontalPager(state = pagerState) {
                if (vm.initialLatestUpdatesData.isNotEmpty() &&
                    vm.latestUpdatesCovers.isNotEmpty() &&
                    vm.latestUpdatesCovers.size > it)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(15.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .haze(hazeState)
                        ) {
                            vm.latestUpdatesCovers[it](
                                ContentScale.FillWidth,
                                Modifier
                                    .fillMaxSize()
                                    .hazeChild(
                                        hazeState,
                                        style = HazeStyle(blurRadius = 8.dp)
                                    )
                            )
                        }
                        LatestUpdateDisplay(
                            title = vm.getTitle(vm.initialLatestUpdatesData[it].attributes.title),
                            desc = vm.getDesc(vm.initialLatestUpdatesData[it].attributes.description),
                            imageHeight = imageHeight,
                            imageWidth = ((2f / 3f) * imageHeight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp,
                                    end = 12.dp
                                )
                        ) { contentScale, modifier ->
                            vm.latestUpdatesCovers[it](contentScale, modifier)
                        }
                    }
            }
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..n) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color = if (i == selected) Color.White else Color.Gray)
            )
        }
    }
}

@Composable
private fun LatestUpdateDisplay(
    title: String,
    desc: String,
    imageHeight: Dp,
    imageWidth: Dp,
    modifier: Modifier = Modifier,
    image: @Composable (ContentScale, Modifier) -> Unit
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
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                lineHeight = 16.sp
            )
            Text(
                desc,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 10.sp
            )
        }
        image(
            ContentScale.FillBounds,
            Modifier
                .height(imageHeight)
                .width(imageWidth)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}

@Composable
private fun BottomAppBar(
    page: Page,
    onPageChange: (Page) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .padding(horizontal = 8.dp)
            .offset(y = (-8).dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.onBackground),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomAppBarIcon(
            page = Page.MAIN,
            imageVector = Assets.Home,
            contentDescription = "main page",
            selected = page == Page.MAIN,
            onClick = onPageChange
        )
        BottomAppBarIcon(
            page = Page.FEED,
            imageVector = Assets.Clipboard,
            contentDescription = "feed page",
            selected = page == Page.FEED,
            onClick = onPageChange
        )
        BottomAppBarIcon(
            page = Page.DISCOVERY,
            imageVector = Assets.Search,
            contentDescription = "discovery page",
            selected = page == Page.DISCOVERY,
            onClick = onPageChange
        )
        BottomAppBarIcon(
            page = Page.USER_LIST,
            imageVector = Assets.`Book-close`,
            contentDescription = "your list page",
            selected = page == Page.USER_LIST,
            onClick = onPageChange
        )
    }
}

@Composable
fun BottomAppBarIcon(
    page: Page,
    imageVector: ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: (Page) -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = if (selected) MaterialTheme.colors.selectedButton
            else MaterialTheme.colors.unselectedButton,
        modifier = modifier
            .size(32.dp)
            .clickable{ onClick(page) }
    )
}

@Composable
private fun ContinueReading(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    if (vm.continueReadingData.isNotEmpty()) Column(modifier = modifier) {
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
                imageVector = Assets.`Chevron-right-bold`,
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
            items(count = vm.continueReadingCovers.size) {
                SmallDisplay(
                    image = { cs, m ->
                        vm.continueReadingCovers[it](cs, m)
                    },
                    title = vm.getTitle(vm.continueReadingData[it].attributes.title),
                    modifier = Modifier.width(screenSize.width / 2)
                )
            }
        }
    }
}

@Composable
private fun SmallDisplay(
    image: @Composable (ContentScale, Modifier) -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        image(
            ContentScale.FillBounds,
            Modifier
                .width(smallDisplayWidth)
                .height(smallDisplayHeight)
                .clip(RoundedCornerShape(5.dp))
        )
        Spacer(Modifier.width(6.dp))
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MangaGenre (
    label: String,
    backgroundGradient: Pair<Color, Color>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(brush = Brush.linearGradient(colors = listOf(
            backgroundGradient.first,
            backgroundGradient.second
        )))
        .padding(top = 16.dp, start = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            GenreDisplay()
        }
    }
}

@Composable
private fun GenreDisplay(
    modifier: Modifier = Modifier
) {
    val width = (screenSize.width / 1.7.dp).dp
    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .height(width + 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(this@BoxWithConstraints.maxWidth / 2)
                    .background(Color.White)
            ) {

            }
        }
    }
}