package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getDesc
import api.mangadex.util.getTagList
import api.mangadex.util.getTitle
import assets.`Book-open`
import assets.`Bookmark-alt`
import assets.`Chevron-right`
import assets.`List-add`
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.delay
import screenSize
import util.BLUR_TINT
import util.Status
import util.edgeToEdge
import viewmodel.DetailViewModel

class DetailScreen(
    private val vm: DetailViewModel
) : Screen {
    @Composable
    override fun Content() {
        vm.init {
            edgeToEdge()
        }
        val nav = LocalNavigator.currentOrThrow
        var p by remember { mutableStateOf(0) }
        LaunchedEffect(vm.popNoticeWidth) {
            delay(2000)
            p = -(vm.popNoticeWidth)
        }
        Scaffold {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val hazeState = remember { HazeState() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
                    .pointerInput(true) {
                        detectHorizontalDragGestures(
                        ) { change, dragAmount ->
                            if (dragAmount > 30) {
                                nav.pop()
                            }
                        }
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize().haze(hazeState)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeChild(
                                hazeState,
                                style = HazeStyle(blurRadius = 6.dp)
                            )
                    ) {
                        Background()
                    }
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().padding(bottom = vm.chapterListHeight.value.dp)
                ) {
                    item {
                        CoverArtDisplay(vm)
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Detail(vm)
                            Text(
                                getDesc(vm.manga.data.attributes.description),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                ChapterList(
                    vm,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
        }
    }

    @Composable
    private fun Pop(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .wrapContentWidth()
        ) {
            Text(
                "Swipe to pop",
                modifier = Modifier.wrapContentWidth()
            )
            Icon(
                imageVector = Assets.`Chevron-right`,
                contentDescription = "back",
                tint = Color.Black
            )
        }
    }

    @Composable
    private fun actionIconColor(isBackgroundFilled: Boolean): Color {
        return if (isBackgroundFilled) MaterialTheme.colors.background else MaterialTheme.colors.secondary
    }

    @Composable
    private fun CoverArtDisplay(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val hazeState = remember { HazeState() }
        val totalHeight = (screenSize.height / 3) - statusBarsHeight
        val backgroundHeight = totalHeight / 1.3f
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        val remainingWidth = screenSize.width - (coverArtWidth + 16.dp)
        val manga = vm.manga
        val attributes = vm.manga.data.attributes
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.haze(hazeState)) {
                BrowseImageNullable(
                    painter = manga.coverArt,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(backgroundHeight)
                        .hazeChild(state = hazeState, style = HazeStyle(
                            blurRadius = 8.dp,
                            tint = BLUR_TINT
                        ))
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .height(backgroundHeight)
                    .width(remainingWidth)
                    .padding(top = vm.titleTagsPadding.value + statusBarsHeight, start = 12.dp, end = 4.dp)
            ) {
                Title(
                    attributes,
                    textLines = {
                        if (it > 1) vm.titleTagsPadding.value = 10.dp
                    }
                )
                Tags(attributes)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
            ) {
                Status(attributes, modifier = Modifier.align(Alignment.End))
                BrowseImageNullable(
                    painter = vm.manga.coverArt,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(coverArtWidth)
                        .height(coverArtHeight)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(totalHeight - backgroundHeight)
                    .width(remainingWidth)
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Action(
                    onClick = {},
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            "Read",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Assets.`Book-open`,
                            contentDescription = "read",
                            tint = MaterialTheme.colors.background
                        )
                    }
                }
                Action(
                    onClick = {},
                    fill = false,
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = Assets.`List-add`,
                        contentDescription = "add to list",
                        tint = actionIconColor(false),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Action(
                    onClick = {},
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = Assets.`Bookmark-alt`,
                        contentDescription = "add to list",
                        tint = actionIconColor(true),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
//            Icon(
//                imageVector = Assets.Info,
//                contentDescription = "detail",
//                tint = Color.White,
//                modifier = Modifier
//                    .padding(top = statusBarsHeight)
//                    .align(Alignment.TopEnd)
//                    .size(totalHeight - coverArtHeight - statusBarsHeight)
//                    .padding(top = 16.dp, end = 12.dp, bottom = 12.dp)
//                    .clickable {  }
//            )
        }
    }

    @Composable
    private fun Title(
        manga: MangaAttributes,
        textLines: (Int) -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        var fontSize by remember { mutableStateOf(28) }
        var maxLines by remember { mutableStateOf(2) }
        var isOverflow by remember { mutableStateOf(false) }
        if (isOverflow) {
            fontSize--
            if (fontSize <= 20) {
                maxLines = 3
            }
        }
        Text(
            getTitle(manga.title),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            onTextLayout = {
                isOverflow = it.hasVisualOverflow
                textLines(it.lineCount)
            }
        )
    }

    @Composable
    private fun Action(
        onClick: () -> Unit,
        fill: Boolean = true,
        verticalPadding: Dp = 2.dp,
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.() -> Unit
    ) {
        val corner = RoundedCornerShape(8.dp)
        val outer: Modifier = if (fill) Modifier.background(color = MaterialTheme.colors.secondary)
            else Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colors.secondary,
                shape = corner
            )
        Box(
            modifier = modifier
                .clip(corner)
                .clickable(onClick = onClick)
                .then(outer)
                .padding(vertical = verticalPadding),
            content = content
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun Tags(
        data: MangaAttributes,
        modifier: Modifier = Modifier
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            getTagList(data).forEach(action = { TagBar(it) })
        }
    }

    @Composable
    private fun TagBar(
        tag: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            tag,
            fontSize = 10.sp,
            modifier = modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color(229, 228, 226, 100))
                .padding(vertical = 1.dp, horizontal = 2.dp)
        )
    }

    @Composable
    private fun Detail(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        val manga = vm.manga.data.attributes
        val rotateDegrees = animateFloatAsState(
            if (!vm.isShowingDetail.value) 90f else 270f
        )
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = vm::detailVisibility)
                .background(MaterialTheme.colors.onBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "More Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    imageVector = Assets.`Chevron-right`,
                    contentDescription = "more information",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotateDegrees.value),
                )
            }
            AnimatedVisibility(vm.isShowingDetail.value) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DetailField("Title", getTitle(manga.title))
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailField(
        field: String,
        detail: String,
        modifier: Modifier = Modifier
    ) {
        Row {
            Text(
                "$field:",
                color = Color.White,
                fontSize = 14.sp,
                modifier = modifier
            )
            Text(
                " $detail",
                color = Color(235, 235, 235),
                fontSize = 14.sp,
                modifier = modifier
            )
        }
    }

    @Composable
    private fun DetailFields(
        fieldDetail: Map<String, String>,
        header: String? = null,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            if (header != null) Text(
                header,
                fontSize = 15.sp,
                color = Color.White,
            )
            fieldDetail.forEach {
                DetailField(it.key, it.value, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }

    private fun status(raw: String): String {
        return when(raw) {
            Status.ON_GOING -> "On Going"
            Status.COMPLETED -> "Completed"
            Status.HIATUS -> "Hiatus"
            Status.CANCELLED -> "Cancelled"
            else -> "Unknown"
        }
    }

    private fun statusColor(rawStatus: String): Color {
        return when(rawStatus) {
            Status.ON_GOING -> Color(0xFF1B663E)
            Status.COMPLETED -> Color( 46, 90, 180)
            Status.HIATUS -> Color.LightGray
            Status.CANCELLED -> Color(255, 160, 0)
            else -> Color.LightGray
        }
    }

    @Composable
    private fun Status(
        manga: MangaAttributes,
        modifier: Modifier = Modifier
    ) {
        if (manga.status != null) Text(
            status(manga.status),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = modifier
                .clip(RoundedCornerShape(5.dp))
                .background(statusColor(manga.status))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }

    @Composable
    private fun Background(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            Icon(
                imageVector = Assets.`Book-open`,
                contentDescription = "background",
                modifier = Modifier
                    .size(screenSize.width / 2)
                    .align(Alignment.BottomEnd)
                    .rotate(-30f)
                    .offset(y = (-40).dp)
            )
        }
    }

    @Composable
    private fun ChapterList(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        Action(
            onClick = vm::navigateToChapterListScreen,
            verticalPadding = 16.dp,
            modifier = modifier
        ) {
            Text(
                "Chapter List",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .onGloballyPositioned {
                        vm.chapterListHeight.value = it.size.height +
                            8 + // parent's bottom padding
                            8 // space for desc
                    }
            )
        }
    }
}