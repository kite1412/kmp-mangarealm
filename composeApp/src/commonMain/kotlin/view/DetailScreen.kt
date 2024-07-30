package view

import Assets
import LocalScreenSize
import LocalSharedViewModel
import LocalWidthClass
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import api.jikan.model.Character
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTitle
import api.mangadex.util.obstruct
import assets.`Book-open`
import assets.`Bookmark-alt`
import assets.`Bookmark-alt-fill`
import assets.`Chevron-right`
import assets.Cross
import assets.`List-add`
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.CustomList
import model.MangaStatus
import model.ScreenSize
import model.Status
import model.WidthClass
import model.session.SessionState
import util.APP_BAR_HEIGHT
import util.appGray
import util.edgeToEdge
import util.getMaxDimension
import util.popNoticeDuration
import util.publicationStatus
import util.publicationStatusColor
import util.resolveCharacterPicture
import util.swipeToPop
import util.toMap
import view_model.DetailScreenModel

class DetailScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { DetailScreenModel(sharedViewModel) }
        LifecycleEffectOnce {
            if (sm.swipeToPopEnabled) {
                val count = SharedObject.popNotifierCount--
                if (count > 0) sm.screenModelScope.launch {
                    sm.showPopNotice = true
                    delay(popNoticeDuration)
                    sm.showPopNotice = false
                }
            }
        }
        val nav = LocalNavigator.currentOrThrow
        Scaffold {
            edgeToEdge()
            BackHandler(enabled = !sm.updating) { nav.pop() }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .swipeToPop(nav, enabled = !sm.updating)
            ) {
                CompositionLocalProvider(LocalScreenSize provides ScreenSize(maxHeight, maxWidth)) {
                    val aboveBottomBar = APP_BAR_HEIGHT + 16.dp
                    val screenSize = LocalScreenSize.current
                    val screenHeight = screenSize.height
                    val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    val coverDisplayHeight = (getMaxDimension() / 2.5f) - statusBarsHeight
                    val fixedDisplayHeight = if (
                        coverDisplayHeight <= screenHeight / 2
                    ) coverDisplayHeight else coverDisplayHeight / 1.5f
                    CoverArtDisplay(sm, nav, fixedDisplayHeight)
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().padding(
                            top = fixedDisplayHeight + 16.dp,
                            bottom = sm.chapterListHeight.dp
                        )
                    ) {
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
                                Characters(sm)
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
                    if (SharedObject.popNotifierCount >= 0) PopNotice(
                        show = sm.showPopNotice,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    if (sm.showUpdateStatus) UpdateStatus(sm)
                    AddToList(sm)
                    Warning(
                        message = sm.warning,
                        height = aboveBottomBar,
                        show = sm.showWarning,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    if (sm.updating) LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Updating...", color = Color.White)
                    }
                    if (sm.showAddListPrompt) AddCustomListPrompt(
                        value = sm.textFieldValue,
                        visibility = sm.visibility,
                        onValueChange = { sm.textFieldValue = it },
                        onVisibilityChange = { sm.visibility = it },
                        onAdd = sm::onAddNewList,
                        onDismiss = sm::onAddListPromptDismiss
                    )
                }
            }
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
        totalHeight: Dp,
        modifier: Modifier = Modifier
    ) {
        val screenSize = LocalScreenSize.current
        val actionHeight = 60.dp
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val backgroundHeight = totalHeight - actionHeight
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        val remainingWidth = screenSize.width - (coverArtWidth + 16.dp)
        val manga = sm.manga
        val data = manga.data
        val attributes = data.attributes
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            val painter = manga.painter
            ImageLoader(
                url = getCoverUrl(data),
                painter = painter,
                contentScale = ContentScale.FillWidth,
                loading = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(backgroundHeight)
            ) { sm.manga = sm.manga.copy(painter = it) }
            Box(Modifier.height(backgroundHeight).obstruct())
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
                    manga = attributes,
                    titleLines = 4,
                    textLines = {
                        if (it > 1) sm.titleTagsPadding = 18
                    }
                )
                Tags(attributes)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
            ) {
                PublicationStatus(attributes, modifier = Modifier.align(Alignment.End))
                BrowseImageNullable(
                    painter = painter,
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
                            .background(Color(230, 230, 230))
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
            val widthClass = LocalWidthClass.current
            BoxWithConstraints(
                Modifier
                    .height(totalHeight - backgroundHeight)
                    .width(remainingWidth)
                    .align(Alignment.BottomStart)
                    .padding(
                        end = 8.dp,
                        top = 8.dp,
                        start = if (widthClass == WidthClass.Compact) 8.dp else 0.dp,
                        bottom = if (widthClass == WidthClass.Compact) 8.dp else 0.dp
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Action(
                        onClick = { sm.onRead(nav) },
                        enabled = !sm.readClicked,
                        verticalPadding = 4.dp,
                        horizontalPadding = 12.dp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .then(
                                if (widthClass == WidthClass.Compact) Modifier.weight(0.5f)
                                    else Modifier
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                "Read",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Assets.`Book-open`,
                                contentDescription = "read",
                                tint = Color.White
                            )
                        }
                    }
                    val applyWeight = Modifier.then(
                        if (widthClass == WidthClass.Compact) Modifier
                            .fillMaxHeight()
                            .weight(0.25f)
                            else Modifier
                    )
                    DetailAction(
                        icon = Assets.`List-add`,
                        contentDescription = "add to list",
                        maxHeight = this@BoxWithConstraints.maxHeight,
                        modifier = applyWeight
                    ) { sm.onAddToList() }
                    DetailAction(
                        icon = if (sm.manga.status == MangaStatus.None) Assets.`Bookmark-alt`
                            else Assets.`Bookmark-alt-fill`,
                        contentDescription = "update status",
                        maxHeight = this@BoxWithConstraints.maxHeight,
                        modifier = applyWeight
                    ) { sm.onStatus() }
                }
            }
        }
    }

    @Composable
    private fun DetailAction(
        icon: ImageVector,
        contentDescription: String,
        maxHeight: Dp,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Action(
            onClick = onClick,
            fill = false,
            verticalPadding = 0.dp,
            modifier = modifier.size(maxHeight)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = actionIconColor(false),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(maxHeight / 2)
            )
        }
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
                InformationBar(
                    label = manga.year.toString(),
                    background = Color.Gray,
                )
            }
            InformationBar(
                label = publicationStatus(manga.status),
                background = publicationStatusColor(manga.status)
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
                sm.navigateToChapter(nav)
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
                StatusBar(it, appGray(), sm.status == it) {
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
        val outer = if (selected) Modifier.background(color) else
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

    @Composable
    private fun AddToList(
        sm: DetailScreenModel,
        modifier: Modifier = Modifier
    ) {
        val screenSize = LocalScreenSize.current
        val offset by animateDpAsState(
            targetValue = if (sm.showAddToList) 0.dp else screenSize.height,
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        )
        val customListSession = sm.sharedViewModel.customListSession
        Column(
            modifier = modifier
                .offset(y = offset)
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) { sm.onAddToListDismiss() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                        .clip(CircleShape)
                        .background(Color(11, 218, 81))
                        .clickable { sm.showAddListPrompt = true }
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Assets.Cross,
                        contentDescription = "create new list",
                        tint = Color.White,
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(45f)
                    )
                    Text(
                        "New list",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colors.onBackground)
                    .padding(16.dp)
            ) {
                var titleHeight by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current
                Text(
                    "Add to your list",
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    onTextLayout = {
                        with(density) { titleHeight = it.size.height.toDp() }
                    },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Icon(
                    imageVector = Assets.Cross,
                    contentDescription = "cancel",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { sm.onAddToListDismiss() }
                )
                when(customListSession.state.value) {
                    SessionState.FETCHING -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    SessionState.ACTIVE -> if (customListSession.data.isNotEmpty()) LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = titleHeight + 16.dp)
                    ) {
                        items(customListSession.data.size) {
                            val customList = customListSession.data[it]
                            CustomList(
                                customList = customList,
                                selected = customList.mangaIds.contains(sm.manga.data.id),
                                onCheckedChange = sm::onCustomListClick
                            )
                        }
                    } else EmptyList("You have no list")
                    else -> {}
                }
            }
        }
    }

    @Composable
    private fun CustomList(
        customList: CustomList,
        selected: Boolean,
        modifier: Modifier = Modifier,
        onCheckedChange: (CustomList) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onCheckedChange(customList) }
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            Text(
                customList.data.value.attributes.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.weight(0.9f)
            )
            Checkbox(
                checked = selected,
                onCheckedChange = { onCheckedChange(customList) },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                ),
                modifier = Modifier.weight(0.1f)
            )
        }
    }
}

@Composable
private fun Characters(
    sm: DetailScreenModel,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val size = Modifier
        .fillMaxWidth()
        .height(screenSize.height / 3.5f)
    Box(
        modifier = modifier
    ) {
        if (!sm.manga.characters.fetched.value) Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .then(size)
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            CircularProgressIndicator()
            Text("Loading characters...")
        }
        else if (sm.manga.characters.data.isNotEmpty()) Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = size
        ) {
            Text(
                "Characters",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sm.manga.characters.data.size) {
                    Character(sm.manga.characters.data[it])
                }
            }
        }
    }
}

@Composable
private fun Character(
    character: Character,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val maxWidth = screenSize.width / 4f
    val imageUrl = resolveCharacterPicture(character)
    val maxHeight = (3f / 2f) * maxWidth
    var painter: Painter? by remember { mutableStateOf(null) }
    if (imageUrl.isNotEmpty()) Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        ImageLoader(
            url = imageUrl,
            painter = painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .width(maxWidth)
                .height(maxHeight)
        ) { p -> painter = p }
        Text(
            character.character.name,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(maxWidth)
        )
    }
}