package view

import LocalScreenSize
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.MangaStatus
import model.Status
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
    Box(modifier = modifier.fillMaxSize()) {
        StatusSelection(
            state = state,
            modifier = Modifier
                .width(screenSize.width / 2)
                .padding(
                    start = 16.dp,
                    top = screenSize.height / 4
                )
        )
    }
}

@Composable
fun List(modifier: Modifier = Modifier) {

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