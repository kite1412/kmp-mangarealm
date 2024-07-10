package view

import Assets
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getTitle
import assets.Search
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.Manga
import util.APP_BAR_HEIGHT
import viewmodel.main.MainViewModel

@Composable
fun Discovery(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.discoveryState
    val nav = LocalNavigator.currentOrThrow
    Box(modifier = modifier.fillMaxSize()) {
        TopBar(
            textFieldValue = state.searchBarValue,
            onSearch = {
                vm.discoveryState.updateSearchData(mapOf(
                    "title" to state.searchBarValue,
                    "includes[]" to "cover_art",
                    "limit" to 100
                ))
            },
            onValueChange = vm.discoveryState::searchBarValueChange
        )
        // TODO change later
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = APP_BAR_HEIGHT + 8.dp,
                    bottom = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
        ) {
            items(vm.discoveryState.searchData.toList()) { p ->
                Display(
                    manga = p.second,
                    onClick = {
                        vm.navigateToDetailScreen(nav, p.second.painter, p.second)
                    }
                ) { vm.discoveryState.updateMangaPainter(p.second, it) }
            }
        }
    }
}

@Composable
private fun TopBar(
    textFieldValue: String,
    onSearch: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_BAR_HEIGHT)
    ) {
        val showPlaceholder = textFieldValue.isEmpty()
        val textColor = if (showPlaceholder) Color.Gray else Color.Black
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.onBackground)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardActions = KeyboardActions(onSearch = onSearch),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.weight(0.9f).padding(start = 8.dp)
                ) {
                    Box {
                        if (textFieldValue.isEmpty()) Text(
                            "Search...",
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                        )
                        it()
                    }
                }
                Icon(
                    imageVector = Assets.Search,
                    contentDescription = "search",
                    tint = Color.Black,
                    modifier = Modifier.weight(0.1f)
                )
            }
        }
    }
}

@Composable
private fun Display(
    manga: Manga,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPainterLoaded: (Painter) -> Unit
) {
    // TODO change later
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = {
                if (manga.painter != null) onClick()
            })
    ) {
        ImageLoader(
            url = getCoverUrl(manga.data),
            painter = manga.painter,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.weight(0.3f),
            onPainterLoaded = onPainterLoaded
        )
        Text(
            getTitle(manga.data.attributes.title),
            fontSize = 24.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .weight(0.7f)
                .padding(top = 16.dp)
        )
    }
}