package view

import Assets
import LocalSharedViewModel
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.request.Visibility
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import assets.Books
import assets.Cross
import assets.`Trash-solid`
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.SwipeAction
import model.session.SessionState
import util.APP_BAR_HEIGHT
import util.popNoticeDuration
import util.session_handler.CustomListSessionHandler
import util.swipeToPop
import util.undoEdgeToEdge
import view_model.CustomListScreenModel
import view_model.main.bottomBarTotalHeight

class CustomListScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { CustomListScreenModel(sharedViewModel) }
        val session = sm.sharedViewModel.customListSession
        LifecycleEffectOnce {
            val count = SharedObject.popNotifierCount--
            if (count > 0) sm.screenModelScope.launch {
                sm.showPopNotice = true
                delay(popNoticeDuration)
                sm.showPopNotice = false
            }
        }
        undoEdgeToEdge()
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when(session.state.value) {
                        SessionState.FETCHING -> LoadingIndicator(modifier = Modifier.align(Alignment.Center)) {
                            Text("Loading...", color = Color.White)
                        }
                        SessionState.ACTIVE -> CustomLists(sm)
                        else -> if (session.response != ListResponse<CustomListAttributes>() && session.data.isEmpty()) EmptyList(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                PopNotice(
                    show = sm.showPopNotice,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Warning(
                    message = sm.warningMessage,
                    height = APP_BAR_HEIGHT,
                    show = sm.showWarning,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                if (sm.showAddPrompt) AddCustomListPrompt(
                    value = sm.textFieldValue,
                    visibility = sm.visibility,
                    onValueChange = { sm.textFieldValue = it },
                    onVisibilityChange = { sm.visibility = it },
                    onAdd = { sm.onAdd() },
                    onDismiss = { sm.showAddPrompt = false }
                )
                if (sm.showUpdateLoading) LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(sm.loadingMessage, color = Color.White)
                }
            }
        }
    }

    @Composable
    private fun TopBar(
        title: String = "My List",
        modifier: Modifier = Modifier,
        action: @Composable BoxScope.() -> Unit = {}
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
                .background(MaterialTheme.colors.background)
        ) {
            Text(
                title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(maxWidth / 1.5f)
            )
            action()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun CustomLists(
        sm: CustomListScreenModel,
        modifier: Modifier = Modifier
    ) {
        val customLists = sm.sharedViewModel.customListSession.data
        val nav = LocalNavigator.currentOrThrow
        val pagerState = rememberPagerState { 2 }
        val scope = rememberCoroutineScope()
        VerticalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = modifier
        ) {
            when (it) {
                0 -> Box(modifier = Modifier.fillMaxSize().swipeToPop(nav)) {
                    if (sm.sharedViewModel.customListSession.data.isNotEmpty()) SessionPagerColumn(
                        session = sm.sharedViewModel.customListSession,
                        handler = CustomListSessionHandler(sm.sharedViewModel.customListSession),
                        contentPadding = PaddingValues(top = APP_BAR_HEIGHT + 8.dp, bottom = bottomBarTotalHeight),
                        modifier = Modifier
                            .fillMaxSize()
                    ) { index ->
                        val customList = customLists[index]
                        AnimatedVisibility(visible = !customList.deleted) {
                            val actions = listOf(
                                SwipeAction(
                                    actionName = "edit",
                                    icon = Icons.Rounded.Edit,
                                    backgroundColor = MaterialTheme.colors.secondary,
                                    action = {}
                                ),
                                SwipeAction(
                                    actionName = "delete",
                                    icon = Assets.`Trash-solid`,
                                    backgroundColor = Color(220, 20, 60),
                                    action = { sm.deleteCustomList(customList, index) }
                                )
                            )
                            Swipeable(
                                actions = actions,
                                oppositeSwipe = { dragAmount ->
                                    if (dragAmount > 30) nav.pop()
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) { m ->
                                Box(
                                    modifier = m
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colors.onBackground)
                                        .align(Alignment.CenterStart)
                                        .clickable {
                                            scope.launch {
                                                sm.fetchManga(index)
                                                pagerState.animateScrollToPage(1)
                                            }
                                        }
                                ) {
                                    Icon(
                                        imageVector = Assets.Books,
                                        contentDescription = "edit",
                                        tint = Color(0xFFA49376),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(90.dp)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                                            modifier = Modifier.fillMaxHeight()
                                        ) {
                                            val visibility = customList.data.attributes.visibility
                                            Text(
                                                visibility.replaceFirstChar { c -> c.uppercaseChar() },
                                                fontWeight = FontWeight.Medium,
                                                color = if (visibility == "private") Color.Red
                                                else Color.Green
                                            )
                                            Text(
                                                "Manga: ${customList.mangaIds.size}",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            customList.data.attributes.name,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else EmptyList(modifier = Modifier.align(Alignment.Center))
                    TopBar()
                    AddCustomList(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 24.dp)
                            .clickable { sm.showAddPrompt = true }
                    )
                }
                1 -> MangaList(sm, pagerState)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun MangaList(
        sm: CustomListScreenModel,
        pagerState: PagerState,
        modifier: Modifier = Modifier
    ) {
        val index = sm.selectedCustomListIndex
        val customList = sm.sharedViewModel.customListSession.data[index]
        val nav = LocalNavigator.currentOrThrow
        val data = customList.manga
        val scope = rememberCoroutineScope()
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .swipeToPop { scope.launch { pagerState.animateScrollToPage(0) } }
        ) {
            if (customList.mangaIds.isNotEmpty())
                if (data.isNotEmpty()) LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(top = APP_BAR_HEIGHT + 8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(data.size) {
                        val manga = data[it]
                        MangaDisplay(
                            manga = manga,
                            parentHeight = maxHeight,
                            onPainterLoaded = { p -> sm.sharedViewModel.updateCustomListMangaPainter(customList, it, p) }
                        ) { sm.navigateToDetail(nav, manga) }
                    }
                } else LoadingIndicator(modifier = Modifier.align(Alignment.Center)) {
                    Text("Loading list...", color = Color.White)
                } else EmptyList(message = "No manga found", modifier = Modifier.align(Alignment.Center))
            TopBar(customList.data.attributes.name) {
                IconButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    Icon(
                        imageVector = Assets.Cross,
                        contentDescription = "back",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun AddCustomListPrompt(
        value: String,
        visibility: Visibility,
        onValueChange: (String) -> Unit,
        onVisibilityChange: (Visibility) -> Unit,
        onAdd: () -> Unit,
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        val onAddWrapper = {
            focusRequester.freeFocus()
            keyboardController?.hide()
            onAdd()
        }
        SideEffect {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(true) {
                    detectTapGestures { onDismiss() }
                }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp,
                    ))
                    .background(MaterialTheme.colors.background)
                    .padding(40.dp)
            ) {
                Text(
                    "Add new list",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { if (value.isNotEmpty()) onAddWrapper() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                VisibilityOptions(
                    visibility = visibility,
                    onClick = onVisibilityChange,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AddCustomListAction(
                        action = "Cancel",
                        color = Color(170, 0, 0),
                        enabled = true,
                        onClick = onDismiss
                    )
                    AddCustomListAction(
                        action = "Add",
                        color = MaterialTheme.colors.secondary,
                        enabled = value.isNotEmpty(),
                        onClick = onAddWrapper
                    )
                }
            }
        }
    }

    @Composable
    private fun VisibilityOptions(
        visibility: Visibility,
        onClick: (Visibility) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            VisibilityOption(
                visibility = Visibility.PRIVATE,
                selected = { visibility == it },
                onClick = { onClick(it) }
            )
            VisibilityOption(
                visibility = Visibility.PUBLIC,
                selected = { visibility == it },
                onClick = { onClick(it) }
            )
        }
    }

    @Composable
    private fun VisibilityOption(
        visibility: Visibility,
        selected: (Visibility) -> Boolean,
        modifier: Modifier = Modifier,
        onClick: (Visibility) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            RadioButton(
                selected = selected(visibility),
                onClick = { onClick(visibility) },
                colors = RadioButtonDefaults.colors(
                    unselectedColor = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                )
            )
            Text(
                visibility.toString().replaceFirstChar { it.uppercaseChar() },
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }

    @Composable
    private fun AddCustomListAction(
        action: String,
        color: Color,
        enabled: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Action(
            onClick = onClick,
            color = color,
            enabled = enabled,
            modifier = modifier
        ) {
            Text(
                action,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}