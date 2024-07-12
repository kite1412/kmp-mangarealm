package view

import Assets
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTagList
import api.mangadex.util.getTitle
import assets.`Book-open`
import assets.`Bookmark-alt`
import assets.`Bookmark-alt-fill`
import assets.`Chevron-right`
import assets.Cross
import assets.`List-add`
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import model.MangaStatus
import model.Status
import screenSize
import util.APP_BAR_HEIGHT
import util.BLUR_TINT
import util.PublicationStatus
import util.edgeToEdge
import util.swipeToPop
import util.toMap
import view_model.DetailScreenModel

class DetailScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        LifecycleEffectOnce {
            SharedObject.popNotifierCount--
        }
        val sm = rememberScreenModel { DetailScreenModel() }
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
                val aboveBottomBar = APP_BAR_HEIGHT + 16.dp
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
                        CoverArtDisplay(sm, nav)
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
                if (sm.showUpdateStatus) UpdateStatus(sm)
                Warning(
                    message = sm.warning,
                    height = aboveBottomBar,
                    show = sm.showWarning,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
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
        nav: Navigator,
        modifier: Modifier = Modifier
    ) {
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val hazeState = remember { HazeState() }
        val totalHeight = (screenSize.height / 2.5f) - statusBarsHeight
        val backgroundHeight = totalHeight / 1.25f
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        val remainingWidth = screenSize.width - (coverArtWidth + 16.dp)
        val data = sm.manga.data
        val attributes = data.attributes
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            val painter = remember { mutableStateOf(SharedObject.detailCover) }
            Box(modifier = Modifier.haze(hazeState)) {
                ImageLoader(
                    url = getCoverUrl(data),
                    painter = painter.value,
                    contentScale = ContentScale.FillWidth,
                    loading = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(backgroundHeight)
                        .hazeChild(state = hazeState, style = HazeStyle(
                            blurRadius = 8.dp,
                            tint = BLUR_TINT
                        ))
                ) {
                    painter.value = it
                    SharedObject.detailCover = it
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(backgroundHeight)
                    .width(remainingWidth)
                    .padding(
                        top = sm.titleTagsPadding.dp + statusBarsHeight,
                        start = 12.dp,
                        end = 4.dp
                    )
            ) {
                Title(
                    attributes,
                    textLines = {
                        if (it > 1) sm.titleTagsPadding = 18
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
                PublicationStatus(attributes, modifier = Modifier.align(Alignment.End))
                BrowseImageNullable(
                    painter = painter.value,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(coverArtWidth)
                        .height(coverArtHeight)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .hazeChild(state = hazeState, style = HazeStyle(
                                blurRadius = 15.dp,
                                tint = BLUR_TINT
                            ))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            CircularProgressIndicator(Modifier.size(16.dp))
                            Text("Loading cover...", fontSize = 10.sp)
                        }
                    }
                }
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
                    onClick = { sm.onRead(nav) },
                    enabled = !sm.readClicked,
                    modifier = Modifier
                        .weight(0.5f)
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
                    verticalPadding = 2.dp,
                    modifier = Modifier
                        .weight(0.25f)
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
                    onClick = { sm.onStatus() },
                    fill = sm.manga.status == MangaStatus.None,
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = if (sm.manga.status == MangaStatus.None) Assets.`Bookmark-alt`
                        else Assets.`Bookmark-alt-fill`,
                        contentDescription = "udpate status",
                        tint = if (sm.manga.status == MangaStatus.None)
                            actionIconColor(true) else MaterialTheme.colors.secondary,
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

    private fun publicationStatus(raw: String): String {
        return when(raw) {
            PublicationStatus.ON_GOING -> "On Going"
            PublicationStatus.COMPLETED -> "Completed"
            PublicationStatus.HIATUS -> "Hiatus"
            PublicationStatus.CANCELLED -> "Cancelled"
            else -> "Unknown"
        }
    }

    private fun publicationStatusColor(rawStatus: String): Color {
        return when(rawStatus) {
            PublicationStatus.ON_GOING -> Color(0xFF1B663E)
            PublicationStatus.COMPLETED -> Color( 46, 90, 180)
            PublicationStatus.HIATUS -> Color.DarkGray
            PublicationStatus.CANCELLED -> Color(150, 0, 0)
            else -> Color.LightGray
        }
    }

    @Composable
    private fun PublicationStatus(
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
                publicationStatus(manga.status),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(publicationStatusColor(manga.status))
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

    @Composable
    private fun UpdateStatus(
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .pointerInput(true) {
                    detectTapGestures {
                        sm.showUpdateStatus = false
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.background)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Update Status",
                            fontStyle = FontStyle.Italic,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        IconButton(
                            onClick = { sm.showUpdateStatus = false }
                        ) {
                            Icon(
                                imageVector = Assets.Cross,
                                contentDescription = "cancel",
                                tint = Color.Red
                            )
                        }
                    }
                    Statuses(sm, modifier = Modifier.padding(horizontal = 8.dp))
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (sm.manga.status != MangaStatus.None) UpdateStatusAction(
                            desc = "Delete",
                            color = Color(170, 0, 0)
                        ) { sm.onDeleteStatus() }
                        UpdateStatusAction(
                            desc = "Update",
                            enabled = sm.manga.status != sm.status
                        ) { sm.onUpdateStatus() }
                    }
                }
            }
        }
    }

    @Composable
    private fun UpdateStatusAction(
        desc: String,
        enabled: Boolean = true,
        color: Color = MaterialTheme.colors.secondary,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Action(
            onClick = onClick,
            color = color,
            enabled = enabled
        ) {
            Text(
                desc,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun Statuses(
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            MangaStatus().forEach {
                StatusBar(it, Color.DarkGray, sm.status == it) {
                    sm.status = it
                }
            }
        }
    }

    @Composable
    private fun StatusBar(
        status: Status,
        color: Color,
        selected: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val textColor = if (selected) Color.White else color
        val corner = RoundedCornerShape(6.dp)
        val outer = if (selected) Modifier.background(Color.DarkGray) else
            Modifier.border(width = 2.dp, color = color, shape = corner)
        Text(
            status.status,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(corner)
                .clickable(onClick = onClick)
                .then(outer)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}