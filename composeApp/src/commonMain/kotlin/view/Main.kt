package view

import Assets
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import assets.`Book-open-outline`
import assets.Logo
import assets.Search
import assets.`Shelf-outline`
import theme.gradient1
import theme.primaryForThick
import util.circleArea

private val navButtonSize = 32.dp
private val searchButtonBoxSize = 64.dp
private val halfSearchButtonBoxSize = circleArea(searchButtonBoxSize.value) / 2

@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            imageVector = Assets.Logo,
                            contentDescription = "logo",
                            modifier = Modifier.size(80.dp),
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.onBackground
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 15.dp,
                    )
                ),
                backgroundColor = MaterialTheme.colors.onBackground,
                cutoutShape = CircleShape
            ) {
                BottomNavigationItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Assets.`Book-open-outline`,
                            contentDescription = "read",
                            modifier = Modifier.size(navButtonSize),
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    label = {
                        Text(
                            "Read",
                            color = MaterialTheme.colors.primary
                        )
                    }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Assets.`Shelf-outline`,
                            contentDescription = "my list",
                            modifier = Modifier.size(navButtonSize),
                            tint = MaterialTheme.colors.primaryForThick
                        )
                    },
                    label = {
                        Text(
                            "My List",
                            color = MaterialTheme.colors.primary
                        )
                    },
                    alwaysShowLabel = false
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(searchButtonBoxSize)
                    .background(brush = Brush.linearGradient(
                        colors = listOf(
                            gradient1[0],
                            gradient1[1],
                        ),
                        start = Offset(x = 0f, y = halfSearchButtonBoxSize),
                        end = Offset(x = halfSearchButtonBoxSize, y = halfSearchButtonBoxSize)
                    ))
                    .clickable {  }
            ) {
                Icon(
                    Assets.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .padding(4.dp),
                    tint = MaterialTheme.colors.onBackground,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {

    }
}