package view

import LocalScreenSize
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.MangaStatus
import model.Status
import shared.adjustStatusBarColor
import theme.selectedButton
import view_model.main.MainViewModel
import view_model.main.state.UserListState

@Composable
fun UserList(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.userListState
    val screenSize = LocalScreenSize.current
    if (state.showOptions) adjustStatusBarColor(Color.White)
        else adjustStatusBarColor(MaterialTheme.colors.background)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
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
    val transition = updateTransition(
        !state.showOptions
    )
    val height by transition.animateDp { if (it) screenSize.height else screenSize.height / 2f }
    val width by transition.animateDp { if (it) screenSize.width else screenSize.width / 2f }
    val rotate by transition.animateFloat { if (it) 0f else -10f }
    val clip by transition.animateDp { if (it) 0.dp else 24.dp }
    val offset by transition.animateDp { if (it) 0.dp else width / 2 }
    val shadowBoxRange = 8.dp
    val shadowBoxCount = 2
    val shadowBoxColors = listOf(
        MaterialTheme.colors.background.copy(alpha = 0.7f),
        MaterialTheme.colors.background.copy(alpha = 0.5f)
    )
    for (i in shadowBoxCount downTo 1) {
        Box(
            modifier = modifier
                .rotate(rotate)
                .offset(x = offset - (shadowBoxRange * i), y = (shadowBoxRange * i))
                .height(height)
                .width(width)
                .clip(RoundedCornerShape(clip))
                .background(shadowBoxColors[i - 1])
        )
    }
    Box(
        modifier = modifier
            .rotate(rotate)
            .offset(x = offset)
            .height(height)
            .width(width)
            .clip(RoundedCornerShape(clip))
            .background(MaterialTheme.colors.background)
    ) {
        TextButton(
            onClick = { state.showOptions = true }
        ) {
            Text("press")
        }
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
            color = MaterialTheme.colors.selectedButton,
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
        if (it) MaterialTheme.colors.onBackground
            else MaterialTheme.colors.background
    }
    val fontColor by transition.animateColor {
        if (it) Color.White else Color.Black
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