package model

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import assets.`Trash-solid`

data class SwipeAction(
    val actionName: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val action: () -> Unit,
)

fun DeleteSwipeAction(action: () -> Unit): SwipeAction = SwipeAction(
    actionName = "delete",
    icon = Assets.`Trash-solid`,
    backgroundColor = Color(220, 20, 60),
    action = action
)