package view

import Assets
import LocalSharedViewModel
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.request.Visibility
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import assets.`Box-open`
import assets.`Trash-solid`
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
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
import view_model.CustomListScreenModel

class CustomListScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { CustomListScreenModel(sharedViewModel) }
        val nav = LocalNavigator.currentOrThrow
        val session = sm.sharedViewModel.customListSession
        LifecycleEffectOnce {
            val count = SharedObject.popNotifierCount--
            if (count > 0) sm.screenModelScope.launch {
                sm.showPopNotice = true
                delay(popNoticeDuration)
                sm.showPopNotice = false
            }
        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .swipeToPop(nav)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when(session.state.value) {
                    SessionState.FETCHING -> LoadingIndicator(modifier = Modifier.align(Alignment.Center)) {
                        Text("Loading...", color = Color.White)
                    }
                    SessionState.ACTIVE -> if (session.data.isNotEmpty()) CustomLists(sm)
                        else EmptyList(modifier = Modifier.align(Alignment.Center))
                    else -> if (session.response != ListResponse<CustomListAttributes>() && session.data.isEmpty()) EmptyList(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                AddCustomList(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 24.dp)
                        .clickable { sm.showAddPrompt = true }
                )
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
    private fun AddCustomList(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colors.secondary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "Add new list",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add new list",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    @Composable
    private fun EmptyList(modifier: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Icon(
                imageVector = Assets.`Box-open`,
                contentDescription = "Empty list",
                modifier = Modifier.size(100.dp)
            )
            Text(
                "Your list is empty",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onBackground
            )
        }
    }

    @Composable
    private fun CustomLists(
        sm: CustomListScreenModel,
        modifier: Modifier = Modifier
    ) {
        val customLists = sm.sharedViewModel.customListSession.data
        val nav = LocalNavigator.currentOrThrow
        SessionPagerColumn(
            session = sm.sharedViewModel.customListSession,
            handler = CustomListSessionHandler(sm.sharedViewModel.customListSession),
            modifier = modifier.fillMaxSize()
        ) {
            val customList = customLists[it]
            AnimatedVisibility(!customList.deleted) {
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
                        action = { sm.deleteCustomList(customList, it) }
                    )
                )
                Swipeable(
                    actions = actions,
                    oppositeSwipe = { dragAmount ->
                        if (dragAmount > 30) nav.pop()
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        customList.data.attributes.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(32.dp)
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
                    .padding(32.dp)
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp)
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
                        onClick = {
                            focusRequester.freeFocus()
                            keyboardController?.hide()
                            onAdd()
                        }
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