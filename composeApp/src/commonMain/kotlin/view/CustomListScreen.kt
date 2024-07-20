package view

import Assets
import LocalScreenSize
import LocalSharedViewModel
import SharedObject
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
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
import model.session.SessionState
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
        val session = sm.session
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
                    SessionState.ACTIVE -> CustomLists(sm)
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
        val data = sm.session.data
        SessionPagerColumn(
            session = sm.session,
            handler = CustomListSessionHandler(sm.session),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            CustomList(customList = data[it]) {

            }
        }
    }

    @Composable
    private fun CustomList(
        customList: Data<CustomListAttributes>,
        modifier: Modifier = Modifier,
        onClick: (Data<CustomListAttributes>) -> Unit,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val leftmostColor = MaterialTheme.colors.secondary
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.5f)
                        .background(leftmostColor)
                )
                CustomListAction(
                    icon = Icons.Rounded.Edit,
                    contentDescription = "edit list",
                    backgroundColor = leftmostColor,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable {  }
                )
                CustomListAction(
                    icon = Assets.`Trash-solid`,
                    contentDescription = "delete list",
                    backgroundColor = Color(220, 20, 60),
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable {  }
                )
            }
            var offset by remember { mutableStateOf(0.dp) }
            val offsetAnimated by animateDpAsState(offset)
            val density = LocalDensity.current
            val screenSize = LocalScreenSize.current
            var show by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .offset(x = offsetAnimated)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.onBackground)
                    .clickable {  }
                    .pointerInput(true) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val maxOffset = -(screenSize.width / 2)
                                offset = if (offset < 0.dp && (!show || offset < maxOffset)) {
                                    show = true
                                    maxOffset
                                } else {
                                    show = false
                                    0.dp
                                }
                            }
                        ) { _, dragAmount ->
                            if (dragAmount < 0) with(density) {
                                offset += dragAmount.toDp()
                            } else if (offset < 0.dp) offset += dragAmount.toDp()
                        }
                    }
            ) {
                Text(
                    customList.attributes.name,
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

    @Composable
    private fun CustomListAction(
        icon: ImageVector,
        contentDescription: String,
        backgroundColor: Color,
        modifier: Modifier = Modifier
    ) {
        Box(modifier = modifier.fillMaxHeight().background(backgroundColor)) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
            )
        }
    }
}