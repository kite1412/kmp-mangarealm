package view

import Assets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.attribute.MangaAttributes
import assets.`Arrow-left-solid`
import assets.`Chevron-right-bold`
import assets.Cross
import assets.`Other-1-solid`
import assets.Search
import assets.`Trash-solid`
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import model.Manga
import model.session.SessionState
import model.session.isNotEmpty
import shared.adjustStatusBarColor
import util.APP_BAR_HEIGHT
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
    adjustStatusBarColor(MaterialTheme.colors.background)
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectTapGestures {
                    if (state.showHistoryOptions) state.showHistoryOptions = false
                }
            }
    ) {
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
                    tint = MaterialTheme.typography.body1.color,
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
                        cursorBrush = SolidColor(MaterialTheme.typography.body1.color),
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
                        tint = MaterialTheme.typography.body1.color,
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
                MangaDisplay(
                    manga = manga,
                    onPainterLoaded = { p ->
                        vm.discoveryState.updateMangaPainter(it, manga, p)
                    },
                    parentHeight = maxHeight
                ) { vm.navigateToDetail(nav, manga) }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Histories(
    state: DiscoveryState,
    modifier: Modifier = Modifier
) {
    val histories = state.histories
    if (histories.isNotEmpty()) Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxSize by remember {
                derivedStateOf {
                    if (state.showAllHistories) histories.size
                    else if (histories.size > 10) 10 else histories.size
                }
            }
            Text(
                if (!state.historyEditing) "Your latest searches" else "Select histories...",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = animateColorAsState(
                    if (state.historyEditing) MaterialTheme.colors.secondary
                        else MaterialTheme.typography.body1.color
                ).value,
                fontStyle = if (state.historyEditing) FontStyle.Italic else FontStyle.Normal,
                modifier = Modifier.padding(start = 4.dp)
            )
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusManager = LocalFocusManager.current
                for (i in 0 until maxSize) {
                    val history = histories[i]
                    Action(
                        onClick = {
                            state.handleHistoryClick(
                                keyboardController = keyboardController,
                                focusManager = focusManager,
                                history = history
                            )
                        },
                        fill = state.selectedHistory.contains(history),
                        horizontalPadding = 12.dp,
                        verticalPadding = 8.dp,
                        color = animateColorAsState(
                            if (state.historyEditing) MaterialTheme.colors.secondary
                            else MaterialTheme.colors.onBackground
                        ).value,
                        corner = CircleShape,
                        onLongClick = {
                            if (!state.historyEditing) state.onEditByLongPress(
                                keyboardController = keyboardController,
                                focusManager = focusManager,
                                history = history
                            )
                        }
                    ) {
                        Text(
                            history,
                            fontWeight = FontWeight.Medium,
                            color =  animateColorAsState(
                                if (state.historyEditing)
                                    if (!state.selectedHistory.contains(history)) MaterialTheme.colors.secondary
                                        else Color.White else MaterialTheme.typography.body1.color
                            ).value,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                val showAll = state.showAllHistories
                val rotate by animateFloatAsState(if (showAll) 180f else 0f)
                if (histories.size > maxSize || showAll) Icon(
                    imageVector = Assets.`Chevron-right-bold`,
                    contentDescription = "more",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 6.dp)
                        .rotate(rotate)
                        .clip(CircleShape)
                        .clickable {
                            state.showAllHistories = !showAll
                        }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(!state.historyEditing) {
                    Icon(
                        imageVector = Assets.`Other-1-solid`,
                        contentDescription = "options",
                        tint = MaterialTheme.typography.body1.color,
                        modifier = Modifier
                            .width(26.dp)
                            .clip(CircleShape)
                            .clickable { state.showHistoryOptions = !state.showHistoryOptions }
                    )
                }
                AnimatedVisibility(state.historyEditing) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(state.selectedHistory.isNotEmpty()) {
                            Icon(
                                imageVector = Assets.`Trash-solid`,
                                contentDescription = "options",
                                tint = Color(180, 0, 0),
                                modifier = Modifier
                                    .width(26.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        state.showDeletionWarning(
                                            message = "Delete selected histories?",
                                            action = state::deleteSelectedHistories
                                        )
                                    }
                            )
                        }
                        Icon(
                            imageVector = Assets.Cross,
                            contentDescription = "cancel",
                            tint = MaterialTheme.typography.body1.color,
                            modifier = Modifier
                                .width(26.dp)
                                .clip(CircleShape)
                                .clickable { state.clearSelectedHistory() }
                        )
                    }
                }
                AnimatedVisibility(
                    visible = state.showHistoryOptions,
                    modifier = Modifier.clickable {}
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        HistoryOption("Edit", modifier = Modifier.fillMaxWidth()) {
                            state.onEditClick()
                        }
                        HistoryOption("Clear", color = Color.Red) {
                            state.showDeletionWarning(
                                message = "Clear all histories?",
                                action = state::clearHistories,
                                deletionLabel = "Yes"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryOption(
    option: String,
    color: Color = Color.Black,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        option,
        color = color,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

@Composable
fun DeletionWarning(
    state: DiscoveryState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .pointerInput(true) {
                detectTapGestures { state.cancelDeletion() }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(MaterialTheme.colors.background)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    state.deletionMessage,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = { state.cancelDeletion() }
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 16.sp
                        )
                    }
                    TextButton(
                        onClick = { state.deletionAction() }
                    ) {
                        Text(
                            state.deletionLabel,
                            fontSize = 16.sp,
                            color = Color(160, 0, 0)
                        )
                    }
                }
            }
        }
    }
}

