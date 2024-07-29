package view.adaptive

import LocalWidthClass
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import model.WidthClass

data class NavigationItemColor(
    val selected: Color,
    val unselected: Color
)

data class NavigationItem(
    val name: String,
    val icon: ImageVector,
    val color: NavigationItemColor,
    val onClick: () -> Unit
)

@Composable
fun AdaptiveNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val widthClass = LocalWidthClass.current
    when (widthClass) {
        WidthClass.Compact -> {}
        WidthClass.Medium -> {}
        WidthClass.Expanded -> {}
    }
}

@Composable
private fun AppNavigationRail(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        NavigationRail {

        }
    }
}