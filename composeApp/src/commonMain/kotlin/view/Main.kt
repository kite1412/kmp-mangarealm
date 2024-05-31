package view

import Assets
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import assets.`Book-open-outline`
import assets.Search
import assets.`Shelf-outline`
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import kotlinx.coroutines.delay
import screenSize
import theme.gradient1
import theme.primaryForThick
import util.circleArea
import viewmodel.MainViewModel

private val navButtonSize = 32.dp
private val searchButtonBoxSize = 64.dp
private val halfSearchButtonBoxSize = circleArea(searchButtonBoxSize.value) / 2
private val latestBarHeight = (screenSize.height.value / 4).dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    initialLatestUpdatesData: List<Data<MangaAttributes>>,
    vm: MainViewModel = MainViewModel()
) {
    if (vm.executeOnce.value) {
        vm.init()
        vm.setExecuteOnce(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 15.dp,
                    )
                ),
                backgroundColor = MaterialTheme.colors.onBackground,
                cutoutShape = CircleShape
            ) {
                BottomNavigationItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Assets.`Book-open-outline`,
                            contentDescription = "read",
                            modifier = Modifier.size(navButtonSize),
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    label = {
                        Text(
                            "Read",
                            color = MaterialTheme.colors.primary
                        )
                    }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Assets.`Shelf-outline`,
                            contentDescription = "my list",
                            modifier = Modifier.size(navButtonSize),
                            tint = MaterialTheme.colors.primaryForThick
                        )
                    },
                    label = {
                        Text(
                            "My List",
                            color = MaterialTheme.colors.primary
                        )
                    },
                    alwaysShowLabel = false
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(searchButtonBoxSize)
                    .background(brush = Brush.linearGradient(
                        colors = listOf(
                            gradient1[0],
                            gradient1[1],
                        ),
                        start = Offset(x = 0f, y = halfSearchButtonBoxSize),
                        end = Offset(x = halfSearchButtonBoxSize, y = halfSearchButtonBoxSize)
                    ))
                    .clickable {  }
            ) {
                Icon(
                    Assets.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .padding(4.dp),
                    tint = MaterialTheme.colors.onBackground,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        LatestUpdatesBar(
            vm = vm,
            data = initialLatestUpdatesData,
            height = latestBarHeight + statusBarPadding
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LatestUpdatesBar(
    vm: MainViewModel,
    data: List<Data<MangaAttributes>>,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }
    val pagerState = rememberPagerState { 10 }
    var isOnManualSwipe by remember { mutableStateOf(false) }
    LaunchedEffect(isOnManualSwipe) {
        while (!isOnManualSwipe) {
            delay(3500)
            if (pagerState.currentPage == pagerState.pageCount - 1) {
                pagerState.animateScrollToPage(
                    0,
                    animationSpec = tween(1000)
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
            .clip(RoundedCornerShape(bottomEnd = 15.dp, bottomStart = 15.dp))
            .background(Color.Transparent)
    ) {
        val res = vm.latestUpdatesCurrentResource
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .haze(hazeState)
        ) {
            if (res.value != null) KamelImage(
                resource = res.value!!,
                contentDescription = "one piece",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .hazeChild(
                        hazeState,
                        style = HazeStyle(blurRadius = 8.dp)
                    )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    start = 8.dp,
                    end = 8.dp,
                )
        ) {
            Text(
                "Latest Updates",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                fontStyle = FontStyle.Italic,
            )
            Spacer(Modifier.height(16.dp))
            HorizontalPager(state = pagerState) {
                BigDisplay(
                    res.value!!,
                    vm.getTitle(data[it].attributes.title),
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea... ",
                    height / 1.8f,
                    // TODO adjust later
                    100.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                )
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
private fun BigDisplay(
    resource: Resource<Painter>,
    title: String,
    desc: String,
    imageHeight: Dp,
    imageWidth: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(end = 4.dp)
                .width(screenSize.width / 1.5f)
        ) {
            Text(
                title,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
            Text(
                desc,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 8.sp
            )
        }
        KamelImage(
            resource = resource,
            contentDescription = title,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(imageHeight)
                .width(imageWidth)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}