package view

import Assets
import LocalSharedViewModel
import SharedObject
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .clickable {  }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "Add new list",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
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
}