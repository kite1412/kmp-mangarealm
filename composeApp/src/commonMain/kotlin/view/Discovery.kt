package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTitle
import assets.`Arrow-left-solid`
import assets.Cross
import assets.Search
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import model.Manga
import model.session.SessionState
import model.session.isNotEmpty
import util.APP_BAR_HEIGHT
import util.publicationDemographic
import util.publicationDemographicColor
import util.publicationStatus
import util.publicationStatusColor
import util.session_handler.MangaSessionHandler
import view_model.main.MainViewModel
import view_model.main.state.DiscoveryState

@OptIn(InternalVoyagerApi::class)
@Composable
fun Discovery(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.discoveryState
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    LaunchedEffect(true) { state.init() }
    Box(modifier = modifier.fillMaxSize()) {
        BackHandler(state.session.isNotEmpty()) {
            if (state.session.isNotEmpty()) state.clearSession()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(
                state = state,
                onSearch = { state.beginSession(keyboardController, focusManager) },
                onBackButtonClick = {
                    state.clearSession()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                onClear = {
                    keyboardController?.show()
                    state.searchBarValue = ""
                },
                onValueChange = vm.discoveryState::searchBarValueChange,
                modifier = Modifier
                    .weight(if (state.session.data.isNotEmpty()) 0.9f else 1f)
            )
        }
        Content(vm, state)
    }
}

@Composable
private fun TopBar(
    state: DiscoveryState,
    onSearch: KeyboardActionScope.() -> Unit,
    onBackButtonClick: () -> Unit,
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_BAR_HEIGHT)
    ) {
        val showPlaceholder = state.searchBarValue.isEmpty()
        val sessionState = state.session.state.value
        val sessionActive = sessionState == SessionState.ACTIVE
        val startPadding by animateDpAsState(if (!sessionActive) 24.dp else 8.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = startPadding,
                    end = 24.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                )
        ) {
            AnimatedVisibility(sessionActive) {
                Icon(
                    imageVector = Assets.`Arrow-left-solid`,
                    contentDescription = "back",
                    tint = Color.Black,
                    modifier = Modifier
                        .weight(0.1f)
                        .size(40.dp)
                        .clickable(onClick = onBackButtonClick)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colors.onBackground)
                    .weight(if (state.session.data.isNotEmpty()) 0.9f else 1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    BasicTextField(
                        value = state.searchBarValue,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.body1.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardActions = KeyboardActions(onSearch = onSearch),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        modifier = Modifier.weight(0.9f).padding(start = 8.dp)
                    ) {
                        Box {
                            if (state.searchBarValue.isEmpty()) Text(
                                "Search...",
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                            )
                            it()
                        }
                    }
                    Icon(
                        imageVector = if (showPlaceholder) Assets.Search else Assets.Cross,
                        contentDescription = "search",
                        tint = Color.Black,
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable { if (!showPlaceholder) onClear() }
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    vm: MainViewModel,
    state: DiscoveryState,
    modifier: Modifier = Modifier
) {
    val nav = LocalNavigator.currentOrThrow
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = APP_BAR_HEIGHT + 8.dp,
                start = 8.dp,
                end = 16.dp
            )
    ) {
        val session = state.session
        val sessionState = session.state.value
        when (sessionState) {
            SessionState.IDLE -> Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Histories(state)
            }
            SessionState.FETCHING -> CircularProgressIndicator(
                Modifier
                    .align(Alignment.Center)
                    .padding(bottom = APP_BAR_HEIGHT)
            )
            SessionState.ACTIVE -> if (session.data.isNotEmpty()) SessionPagerColumn<Manga, MangaAttributes>(
                session = state.session,
                state = state.listState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = APP_BAR_HEIGHT + 16.dp),
                handler = MangaSessionHandler(state.session),
                onSessionLoaded = state::onSessionLoaded,
                modifier = Modifier.fillMaxSize()
            ) {
                val manga = state.session.data[it]
                Display(
                    manga = manga,
                    onPainterLoaded = { p ->
                        vm.discoveryState.updateMangaPainter(it, manga, p)
                    },
                    parentHeight = maxHeight
                ) { vm.navigateToDetailScreen(nav, manga) }
            } else Text(
                "No results found",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = APP_BAR_HEIGHT),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun Histories(
    state: DiscoveryState,
    modifier: Modifier = Modifier
) {
    val histories = state.histories
    if (histories.isNotEmpty()) Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        val size by remember {
            derivedStateOf { if (histories.size > 10) 10 else histories.size }
        }
        Text(
            "Your latest searches",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp)
        )
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current
            for (i in 0 until size) {
                val history = histories[i]
                Action(
                    onClick = { state.beginSession(keyboardController, focusManager, history) },
                    fill = false,
                    horizontalPadding = 10.dp,
                    verticalPadding = 8.dp,
                    color = MaterialTheme.colors.onBackground,
                    corner = CircleShape,
                    onDoubleClick = { state.deleteHistory(history) }
                ) {
                    Text(
                        history,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun Display(
    manga: Manga,
    parentHeight: Dp,
    onPainterLoaded: (Painter) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val height = parentHeight / 5f
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        ) {
            ImageLoader(
                url = getCoverUrl(manga.data),
                painter = manga.painter,
                contentScale = ContentScale.FillBounds,
                onPainterLoaded = onPainterLoaded,
                loading = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.6f))
                    ) {
                        CircularProgressIndicator(Modifier.size(18.dp).align(Alignment.Center))
                    }
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(0.7f)
                .padding(top = 4.dp)
        ) {
            val attributes = manga.data.attributes
            Text(
                getTitle(attributes.title),
                maxLines = 2,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (attributes.status != null) Information(
                    label = publicationStatus(attributes.status),
                    background = publicationStatusColor(attributes.status)
                )
                if (attributes.publicationDemographic != null) Information(
                    label = publicationDemographic(attributes.publicationDemographic),
                    background = publicationDemographicColor(attributes.publicationDemographic)
                )
                if (attributes.year != null) Information(
                    label = attributes.year.toString(),
                    background = Color.Gray
                )
            }
            Spacer(Modifier.height(0.dp))
            Text(
                getDesc(attributes.description),
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun Information(
    label: String,
    background: Color,
    modifier: Modifier = Modifier
) {
    InformationBar(
        label = label,
        background = background,
        fontSize = 10.sp,
        clip = RoundedCornerShape(2.dp),
        modifier = modifier
    )
}