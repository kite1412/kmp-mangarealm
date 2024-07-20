package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SwipeAction(
    val actionName: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val action: () -> Unit,
)
