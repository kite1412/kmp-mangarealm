package view.adaptive

import LocalWidthClass
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Route
import model.WidthClass
import theme.darkBeige
import util.APP_BAR_HEIGHT
import util.isDarkMode

val NAVIGATION_RAIL_WIDTH = 72.dp
val NAVIGATION_DRAWER_WIDTH = 256.dp

private val selectedRouteBackgroundLight = Color(0xFFA08E72)
private val selectedRouteBackgroundDark = darkBeige
private val unselectedRouteColorLight = Color(0xFFD1C5B4)
private val unselectedRouteColorDark = Color(0xFFE2D6C6)
private val selectedRouteColor = Color(0xFF322C00)

@Composable
private fun navigationBarBackgroundColor(): Color = MaterialTheme.colors.onBackground

@Composable
private fun contentColor(selected: Boolean): Color = if (selected) selectedRouteColor
    else if (isDarkMode()) unselectedRouteColorDark else unselectedRouteColorLight

@Composable
fun AdaptiveNavigationBar(
    selectedRoute: Route,
    routes: List<Route>,
    showNavigationBar: Boolean = true,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    when (val widthClass = LocalWidthClass.current) {
        WidthClass.Compact -> AppBottomNavigationBar(
            selected = selectedRoute,
            routes = routes,
            onRouteClick = onRouteSelected,
            showNavigationBar = showNavigationBar,
            modifier = modifier,
            content = content
        )
        else -> AppNavigationRail(
            selected = selectedRoute,
            routes = routes,
            onRouteClick = onRouteSelected,
            isDrawer = widthClass == WidthClass.Expanded,
            showNavigationBar = showNavigationBar,
            modifier = modifier,
            content = content
        )
    }
}

@Composable
private fun RouteIcon(
    route: Route,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = route.icon,
        contentDescription = route.name,
        tint = contentColor(selected),
        modifier = modifier
    )
}

@Composable
private fun AppNavigationRail(
    selected: Route,
    routes: List<Route>,
    isDrawer: Boolean,
    onRouteClick: (Route) -> Unit,
    showNavigationBar: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = showNavigationBar,
            modifier = Modifier
                .width(
                    if (isDrawer) NAVIGATION_DRAWER_WIDTH else NAVIGATION_RAIL_WIDTH
                )
                .fillMaxHeight()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(navigationBarBackgroundColor())
                    .padding(8.dp)
            ) {
                val contentWidth = Modifier.composed {
                    if (isDrawer) fillMaxWidth() else Modifier
                }
                routes.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            if (isDrawer) 16.dp else 0.dp
                        ),
                        modifier = Modifier
                            .then(contentWidth)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selected == it) if (isDarkMode()) selectedRouteBackgroundDark else selectedRouteBackgroundLight
                                    else navigationBarBackgroundColor()
                            )
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) { onRouteClick(it) }
                            .padding(8.dp)
                    ) {
                        RouteIcon(
                            route = it,
                            selected = selected == it
                        )
                        if (isDrawer) Text(
                            it.name,
                            color = contentColor(selected == it),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
private fun AppBottomNavigationBar(
    selected: Route,
    routes: List<Route>,
    onRouteClick: (Route) -> Unit,
    showNavigationBar: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(
            visible = showNavigationBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .offset(y = (-8).dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(navigationBarBackgroundColor())
                    .clickable(enabled = false) {},
            ) {
                routes.forEach {
                    RouteIcon(
                        route = it,
                        selected = selected == it,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) { onRouteClick(it) }
                    )
                }
            }
        }
    }
}