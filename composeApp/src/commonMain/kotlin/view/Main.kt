package view

import Assets
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.onGloballyPositioned
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
import assets.`Book-close`
import assets.`Chevron-right`
import assets.Clipboard
import assets.Home
import assets.Person
import assets.Search
import assets.`Text-align-right`
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
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
private val tagsDisplayWidth = (screenSize.width / 1.3.dp).dp
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
fun MainScreen(vm: MainViewModel = MainViewModel()) {
    vm.init()
    val latestUpdatesPagerState = rememberPagerState { 10 }
    autoSlideLatestUpdates(latestUpdatesPagerState)
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
                            pagerState = latestUpdatesPagerState
                        )
                    }
                }
                item {
                    ContinueReading(vm = vm)
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
                            mangaList = vm.romcomManga,
                            label = "Rom-Com",
                            backgroundGradient = Brush.linearGradient(colors = listOf(
                                Color(0xFFADD8E6),
                                Color(0xFFFFB6C1)
                            )),
                            modifier = mangaTagsModifier
                        )
                        MangaTags(
                            mangaList = vm.advComManga,
                            label = "Adventure-Comedy",
                            backgroundGradient = Brush.linearGradient(colors = listOf(
                                Color(0xFFFFA07A),
                                Color(0xFFFFC48C)
                            )),
                            modifier = mangaTagsModifier
                        )
                        MangaTags(
                            mangaList = vm.psyMysManga,
                            label = "Psychological-Mystery",
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun autoSlideLatestUpdates(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(pagerState.settledPage) {
        while (true) {
            delay(LATEST_UPDATE_SLIDE_TIME.toLong())
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
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }

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
                                        style = HazeStyle(
                                            blurRadius = 8.dp,
                                            tint = Color.LightGray.copy(alpha = 0.4f)
                                        )
                                    )
                            )
                        }
                        LatestUpdateDisplay(
                            title = getTitle(vm.initialLatestUpdatesData[it].attributes.title),
                            desc = getDesc(vm.initialLatestUpdatesData[it].attributes.description),
                            imageHeight = imageHeight,
                            imageWidth = (imageHeight * imageRatio),
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
            items(count = vm.continueReadingCovers.size) {
                SmallDisplay(
                    image = { cs, m ->
                        vm.continueReadingCovers[it](cs, m)
                    },
                    title = getTitle(vm.continueReadingData[it].attributes.title),
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
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MangaTags (
    mangaList: List<Data<MangaAttributes>>,
    label: String,
    backgroundGradient: Brush,
    unselectedDotColor: Color = Color(0xFFDEDEDE),
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { 10 }
    var labelHeight by remember { mutableStateOf(0) }
    Box(modifier = modifier
        .fillMaxWidth()
        .height(labelHeight.dp + (tagsDisplayWidth * imageRatio))
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
                Text(
                    label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.onGloballyPositioned {
                        labelHeight = it.size.height
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
            if (mangaList.isNotEmpty()) HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(bottom = 16.dp),
                pageSize = PageSize.Fixed(tagsDisplayWidth),
                pageSpacing = 16.dp,
                verticalAlignment = Alignment.Bottom
            ) {
                TagsDisplay(
                    manga = mangaList[it],
                    contentWidth = tagsDisplayWidth,
                    isSelected = it == pagerState.currentPage,
                    modifier = Modifier.offset(x = if (pagerState.currentPage == 9)
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
    contentWidth: Dp,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val notSelectedWidth = contentWidth - (contentWidth / 10)
    val width by animateDpAsState(targetValue = if (isSelected) contentWidth else notSelectedWidth)
    val height by animateDpAsState(targetValue = if (isSelected) contentWidth * imageRatio else notSelectedWidth * imageRatio)
    val obstructColor by animateColorAsState(if (!isSelected) Color.Black else Color.Transparent)
    BoxWithConstraints(
        modifier = modifier
            .width(width)
            .height(height)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(width / 2.3f)
                    .height(height)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White)
            ) {
               AutoSizeBox(
                   url = getCoverUrl(manga),
                   modifier = Modifier.fillMaxSize()
               ) {
                   when (it) {
                       is ImageAction.Loading -> {
                           CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                       }
                       is ImageAction.Success -> {
                           Image(
                               painter = rememberImageSuccessPainter(it),
                               contentDescription = "cover art",
                               modifier = Modifier.fillMaxSize(),
                               contentScale = ContentScale.FillBounds
                           )
                       }
                       else -> {
                           Text("Can't load image", fontSize = 10.sp)
                       }
                   }
               }
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