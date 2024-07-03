package view

import Assets
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import screenSize
import util.BLUR_TINT
import util.Status
import util.edgeToEdge
import util.swipeToPop
import util.toMap
import viewmodel.DetailScreenModel

class DetailScreen : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        LifecycleEffectOnce {
            SharedObject.popNotifierCount--
        }
        val sm = rememberScreenModel { DetailScreenModel(SharedObject.detailManga) }
        edgeToEdge()
        val nav = LocalNavigator.currentOrThrow
        Scaffold {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
                    .swipeToPop(nav)
            ) {
                val hazeState = remember { HazeState() }
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
                    modifier = Modifier.fillMaxSize().padding(bottom = sm.chapterListHeight.dp)
                ) {
                    item {
                        CoverArtDisplay(sm)
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Detail(sm)
                            Text(
                                getDesc(sm.manga.data.attributes.description),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                ChapterList(
                    sm,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                if (SharedObject.popNotifierCount >= 0) Pop(sm, modifier = Modifier.align(Alignment.CenterStart))
            }
        }
    }

    @Composable
    private fun Pop(
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        val density = LocalDensity.current
        val popAnimation by animateDpAsState(sm.popNoticeWidth.dp)
        val startPadding = 4.dp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .offset(x = popAnimation)
                .padding(start = startPadding)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                .onGloballyPositioned {
                    with(density) {
                        val width = it.size.width.toDp().value
                        val additionalWidth = startPadding + 12.dp
                        sm.animatePopNotice(width, additionalWidth)
                    }
                }
        ) {
            Text(
                "Swipe to pop",
                color = Color.White
            )
            Icon(
                imageVector = Assets.`Chevron-right`,
                contentDescription = "back",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    @Composable
    private fun actionIconColor(isBackgroundFilled: Boolean): Color {
        return if (isBackgroundFilled) MaterialTheme.colors.background else MaterialTheme.colors.secondary
    }

    @Composable
    private fun CoverArtDisplay(
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val hazeState = remember { HazeState() }
        val totalHeight = (screenSize.height / 3) - statusBarsHeight
        val backgroundHeight = totalHeight / 1.3f
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        val remainingWidth = screenSize.width - (coverArtWidth + 16.dp)
        val attributes = sm.manga.data.attributes
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.haze(hazeState)) {
                BrowseImageNullable(
                    painter = SharedObject.detailCover,
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(backgroundHeight)
                    .width(remainingWidth)
                    .padding(top = sm.titleTagsPadding.dp + statusBarsHeight, start = 12.dp, end = 4.dp)
            ) {
                Title(
                    attributes,
                    textLines = {
                        if (it > 1) sm.titleTagsPadding = 10
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
                    painter = SharedObject.detailCover,
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
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        val manga = sm.manga.data.attributes
        val rotateDegrees = animateFloatAsState(
            if (!sm.isShowingDetail) 90f else 270f
        )
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = sm::detailVisibility)
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
            AnimatedVisibility(sm.isShowingDetail) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DetailField("Title", getTitle(manga.title))
                        DetailField("Alt Titles", manga.altTitles.toMap().values.toSet().joinToString(", "))
                        DetailField("Publication Year", manga.year)
                        DetailField("Demographic", manga.publicationDemographic?.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }
    }

    @Composable
    private fun DetailField(
        field: String,
        value: Any?,
        modifier: Modifier = Modifier
    ) {
        if (value != null) Row {
            Text(
                "$field:",
                color = Color.White,
                fontSize = 14.sp,
                modifier = modifier
            )
            Text(
                " $value",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = modifier
            )
        }
    }

    @Composable
    private fun DetailField(
        field: String,
        value: String?,
        modifier: Modifier = Modifier
    ) {
        if (!value.isNullOrEmpty()) Row {
            Text(
                "$field:",
                color = Color.White,
                fontSize = 14.sp,
                modifier = modifier
            )
            Text(
                " $value",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
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
            Status.HIATUS -> Color.DarkGray
            Status.CANCELLED -> Color(150, 0, 0)
            else -> Color.LightGray
        }
    }

    @Composable
    private fun Status(
        manga: MangaAttributes,
        modifier: Modifier = Modifier
    ) {
        if (manga.status != null) Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier
        ) {
            if (manga.year != null) {
                Text(
                    manga.year.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Gray)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Text(
                status(manga.status),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(statusColor(manga.status))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
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
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        val density = LocalDensity.current
        val nav = LocalNavigator.currentOrThrow
        Action(
            onClick = {
                sm.navigateToChapterListScreen(nav)
            },
            verticalPadding = 16.dp,
            modifier = modifier
                .onGloballyPositioned {
                    with(density) {
                        sm.chapterListHeight = it.size.height.toDp().value.toInt() +
                                8 + // parent's bottom padding
                                8 // space for desc
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