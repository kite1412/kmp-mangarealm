package view

import Assets
import LocalScreenSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assets.`Settings-horizontal`
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.MangaStatus
import model.Status
import shared.adjustStatusBarColor
import util.APP_BAR_HEIGHT
import util.appGray
import view_model.main.MainViewModel
import view_model.main.bottomBarTotalHeight
import view_model.main.state.UserListState

@Composable
fun UserList(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.userListState
    val screenSize = LocalScreenSize.current
    if (state.showOptions) adjustStatusBarColor(MaterialTheme.colors.onBackground)
        else adjustStatusBarColor(MaterialTheme.colors.background)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.onBackground)
    ) {
        StatusSelection(
            state = state,
            modifier = Modifier
                .width(screenSize.width / 2)
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )
        List(
            state = state,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun List(
    state: UserListState,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val transition = updateTransition(!state.showOptions)
    val height by transition.animateDp { if (it) screenSize.height else screenSize.height * state.sizeRatio }
    val width by transition.animateDp { if (it) screenSize.width else screenSize.width * state.sizeRatio }
    val rotate by transition.animateFloat { if (it) 0f else -10f }
    val clip by transition.animateDp { if (it) 0.dp else 24.dp }
    val offset by transition.animateDp { if (it) 0.dp else width / 2 }
    val shadowBoxRangeHeight = 16.dp
    val shadowBoxRangeWidth = 8.dp
    val shadowBoxCount = 2
    val shadowBoxColors = listOf(
        MaterialTheme.colors.background.copy(alpha = 0.7f),
        MaterialTheme.colors.background.copy(alpha = 0.5f)
    )
    for (i in shadowBoxCount downTo 1) {
        AnimatedVisibility(
            visible = state.showOptions,
            modifier = modifier
                .rotate(rotate - 2 * i)
                .offset(x = offset - (shadowBoxRangeWidth * i))
                .height(height - shadowBoxRangeHeight * i)
                .width(width)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(clip))
                    .background(shadowBoxColors[i - 1])
            )
        }
    }
    BoxWithConstraints(
        modifier = modifier
            .rotate(rotate)
            .offset(x = offset)
            .height(height)
            .width(width)
            .clip(RoundedCornerShape(clip))
            .background(MaterialTheme.colors.background)
    ) {
        val maxHeight = maxHeight
        val maxWidth = maxWidth
        Column {
            TopBar(state)
            ListContent(
                state = state,
                parentHeight = if (maxHeight >= maxWidth) maxHeight else maxWidth,
                modifier = Modifier.padding(8.dp)
            )
        }
        if (state.showOptions) Box(
            Modifier
                .fillMaxSize()
                .clickable(
                    enabled = state.selectedStatus != MangaStatus.None,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ) { state.onOptionDismiss() }
        )
    }
}

@Composable
private fun TopBar(
    state: UserListState,
    modifier: Modifier = Modifier
) {
    val defaultHeight = APP_BAR_HEIGHT
    val sizeRatio = state.sizeRatio
    val transition = updateTransition(sizeRatio)
    val height by transition.animateDp { defaultHeight * it }
    val startPadding by transition.animateDp { 24.dp * sizeRatio }
    val iconSize by transition.animateDp { 30.dp * sizeRatio }
    val headerSize by transition.animateFloat { 24 * sizeRatio }
    Box(
        modifier = modifier.fillMaxWidth().height(height)
    ) {
        if (state.selectedStatus != MangaStatus.None) Icon(
            imageVector = Assets.`Settings-horizontal`,
            contentDescription = "show options",
            tint = MaterialTheme.typography.body1.color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = startPadding)
                .size(iconSize)
                .clickable { state.showOptions() }
        )
        Text(
            state.selectedStatus.status,
            fontSize = headerSize.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ListContent(
    state: UserListState,
    parentHeight: Dp,
    modifier: Modifier = Modifier
) {
    val manga = state.manga
    val nav = LocalNavigator.currentOrThrow
    if (manga.isNotEmpty()) LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = bottomBarTotalHeight),
        modifier = modifier.fillMaxSize()
    ) {
        items(manga.size) {
            DynamicMangaDisplay(
                manga = manga[it],
                parentHeight = parentHeight,
                ratio = state.sizeRatio,
                onPainterLoaded = { p ->
                    state.sharedViewModel.onMangaStatusPainterLoaded(
                        status = state.selectedStatus,
                        painter = p,
                        index = it
                    )
                }
            ) { state.vm.navigateToDetail(nav, manga[it]) }
        }
    } else Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "List is empty",
            fontWeight = FontWeight.Medium,
            fontSize = animateFloatAsState(18 * state.sizeRatio).value.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun StatusSelection(
    state: UserListState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        Text(
            "Select Status",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
        )
        StatusOptions(state)
    }
}

@Composable
private fun StatusOptions(
    state: UserListState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        MangaStatus(true).forEach {
            StatusOption(
                status = it,
                selected = state.selectedStatus == it,
                onClick = { state.onStatusSelected(it) }
            )
        }
    }
}

@Composable
private fun StatusOption(
    status: Status,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(selected)
    val background by transition.animateColor {
        if (it) MaterialTheme.colors.secondary
            else MaterialTheme.colors.background.copy(alpha = 0.7f)
    }
    val fontColor by transition.animateColor {
        if (it) Color.White else appGray()
    }
    Text(
        text = status.status,
        color = fontColor,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}