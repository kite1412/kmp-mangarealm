package view

import Assets
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getCoverUrl
import api.mangadex.util.getDesc
import api.mangadex.util.getTitle
import assets.Cross
import assets.Search
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import model.Manga
import util.APP_BAR_HEIGHT
import util.publicationDemographic
import util.publicationDemographicColor
import util.publicationStatus
import util.publicationStatusColor
import util.session_handler.MangaSessionHandler
import view_model.main.MainViewModel

@Composable
fun Discovery(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state = vm.discoveryState
    val nav = LocalNavigator.currentOrThrow
    Box(modifier = modifier.fillMaxSize()) {
        val sessionHandler = remember(
            state.session.data,
            state.session.response
        ) { MangaSessionHandler(state.session) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        TopBar(
            textFieldValue = state.searchBarValue,
            onSearch = {
                if (state.searchBarValue.isNotEmpty()) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    vm.discoveryState.updateSession(
                        queries = mapOf(
                            "title" to state.searchBarValue,
                            "includes[]" to "cover_art",
                            "limit" to 50
                        )
                    )
                }
            },
            onClear = { state.searchBarValue = "" },
            onValueChange = vm.discoveryState::searchBarValueChange
        )
        if (state.session.data.isNotEmpty()) BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = APP_BAR_HEIGHT + 8.dp,
                    bottom = 8.dp,
                    start = 8.dp,
                    end = 16.dp
                )
        ) {
            SessionPagerColumn<Manga, MangaAttributes>(
                session = state.session,
                state = state.listState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = APP_BAR_HEIGHT + 16.dp),
                handler = sessionHandler,
                onSessionLoaded = state::onSessionLoaded,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val manga = state.session.data[it]
                Display(
                    manga = manga,
                    onPainterLoaded = { p ->
                        vm.discoveryState.updateMangaPainter(it, manga, p)
                    },
                    parentHeight = maxHeight
                ) { vm.navigateToDetailScreen(nav, manga.painter, manga) }
            }
        }
    }
}

@Composable
private fun TopBar(
    textFieldValue: String,
    onSearch: KeyboardActionScope.() -> Unit,
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_BAR_HEIGHT)
    ) {
        val showPlaceholder = textFieldValue.isEmpty()
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
                    imageVector = if (showPlaceholder) Assets.Search else Assets.Cross,
                    contentDescription = "search",
                    tint = Color.Black,
                    modifier = Modifier
                        .weight(0.1f)
                        .clickable { if (!showPlaceholder) onClear() }
                )
            }
        }
    }
}

@Composable
private fun Display(
    manga: Manga,
    parentHeight: Dp,
    onPainterLoaded: (Painter) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val height = parentHeight / 5f
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick)
    ) {
        ImageLoader(
            url = getCoverUrl(manga.data),
            painter = manga.painter,
            contentScale = ContentScale.FillBounds,
            onPainterLoaded = onPainterLoaded,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .weight(0.3f),
        )
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(0.7f)
                .padding(top = 4.dp)
        ) {
            val attributes = manga.data.attributes
            Text(
                getTitle(attributes.title),
                maxLines = 2,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (attributes.status != null) Information(
                    label = publicationStatus(attributes.status),
                    background = publicationStatusColor(attributes.status)
                )
                if (attributes.publicationDemographic != null) Information(
                    label = publicationDemographic(attributes.publicationDemographic),
                    background = publicationDemographicColor(attributes.publicationDemographic)
                )
                if (attributes.year != null) Information(
                    label = attributes.year.toString(),
                    background = Color.Gray
                )
            }
            Spacer(Modifier.height(0.dp))
            Text(
                getDesc(attributes.description),
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun Information(
    label: String,
    background: Color,
    modifier: Modifier = Modifier
) {
    InformationBar(
        label = label,
        background = background,
        fontSize = 10.sp,
        clip = RoundedCornerShape(2.dp),
        modifier = modifier
    )
}