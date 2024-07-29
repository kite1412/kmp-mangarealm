package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ItemColor(
    val selected: Color,
    val unselected: Color
)

data class Route(
    val name: String,
    val icon: ImageVector,
    val color: ItemColor
)