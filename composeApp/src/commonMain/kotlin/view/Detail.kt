package view

import Assets
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getDesc
import api.mangadex.util.getTagList
import api.mangadex.util.getTitle
import assets.`Book-open`
import assets.`Bookmark-alt`
import assets.`Chevron-right`
import assets.Info
import assets.`List-add`
import cafe.adriel.voyager.core.screen.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import model.Manga
import screenSize
import util.BLUR_TINT
import util.edgeToEdge
import viewmodel.DetailViewModel

class DetailScreen(
    private val vm: DetailViewModel
) : Screen {
    @Composable
    override fun Content() {
        vm.init {
            edgeToEdge()
        }
        Scaffold {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val hazeState = remember { HazeState() }
            Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
                Box(modifier = Modifier.fillMaxSize().haze(hazeState)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeChild(
                                hazeState,
                                style = HazeStyle(blurRadius = 6.dp)
                            )
                    ) {
                        Background()
                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        CoverArtDisplay(vm)
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Detail(vm)
                            Text(
                                getDesc(vm.manga.data.attributes.description),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun actionIconColor(isBackgroundFilled: Boolean): Color {
        return if (isBackgroundFilled) MaterialTheme.colors.background else MaterialTheme.colors.secondary
    }

    @Composable
    private fun CoverArtDisplay(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val hazeState = remember { HazeState() }
        val totalHeight = (screenSize.height / 3) - statusBarsHeight
        val backgroundHeight = totalHeight / 1.3f
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        val remainingWidth = screenSize.width - (coverArtWidth + 16.dp)
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.haze(hazeState)) {
                BrowseImageNullable(
                    painter = vm.manga.coverArt,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(backgroundHeight)
                        .hazeChild(state = hazeState, style = HazeStyle(
                            blurRadius = 8.dp,
                            tint = BLUR_TINT
                        ))
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .height(backgroundHeight)
                    .width(remainingWidth)
                    .padding(top = vm.titleTagsPadding.value + statusBarsHeight, start = 12.dp, end = 4.dp)
            ) {
                Title(
                    vm.manga,
                    textLines = {
                        if (it > 1) vm.titleTagsPadding.value = 10.dp
                    }
                )
                Tags(vm.manga.data)
            }
            BrowseImageNullable(
                painter = vm.manga.coverArt,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
                    .width(coverArtWidth)
                    .height(coverArtHeight)
                    .clip(RoundedCornerShape(8.dp))
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(totalHeight - backgroundHeight)
                    .width(remainingWidth)
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Action(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            "Read",
                            color = MaterialTheme.colors.background,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Assets.`Book-open`,
                            contentDescription = "read",
                            tint = MaterialTheme.colors.background
                        )
                    }
                }
                Action(
                    fill = false,
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = Assets.`List-add`,
                        contentDescription = "add to list",
                        tint = actionIconColor(false),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Action(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = Assets.`Bookmark-alt`,
                        contentDescription = "add to list",
                        tint = actionIconColor(true),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Icon(
                imageVector = Assets.Info,
                contentDescription = "detail",
                tint = Color.White,
                modifier = Modifier
                    .padding(top = statusBarsHeight)
                    .align(Alignment.TopEnd)
                    .size(totalHeight - coverArtHeight - statusBarsHeight)
                    .padding(top = 16.dp, end = 12.dp, bottom = 12.dp)
                    .clickable {  }
            )
        }
    }

    @Composable
    private fun Title(
        manga: Manga,
        textLines: (Int) -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        var fontSize by remember { mutableStateOf(28) }
        var maxLines by remember { mutableStateOf(2) }
        var isOverflow by remember { mutableStateOf(false) }
        if (isOverflow) {
            fontSize--
            if (fontSize <= 20) {
                maxLines = 3
            }
        }
        Text(
            getTitle(manga.data.attributes.title),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            onTextLayout = {
                isOverflow = it.hasVisualOverflow
                textLines(it.lineCount)
            }
        )
    }

    @Composable
    private fun Action(
        fill: Boolean = true,
        verticalPadding: Dp = 2.dp,
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.() -> Unit
    ) {
        val corner = RoundedCornerShape(8.dp)
        val outer: Modifier = if (fill) Modifier.background(color = MaterialTheme.colors.secondary)
            else Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colors.secondary,
                shape = corner
            )
        Box(
            modifier = modifier
                .clip(corner)
                .then(outer)
                .padding(vertical = verticalPadding),
            content = content
        )
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun Tags(
        data: Data<MangaAttributes>,
        modifier: Modifier = Modifier
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            getTagList(data).forEach(action = { TagBar(it) })
        }
    }

    @Composable
    private fun TagBar(
        tag: String,
        modifier: Modifier = Modifier
    ) {
        Text(
            tag,
            fontSize = 10.sp,
            modifier = modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color(229, 228, 226, 100))
                .padding(vertical = 1.dp, horizontal = 2.dp)
        )
    }

    @Composable
    private fun Detail(
        vm: DetailViewModel,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.onBackground)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "More Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    imageVector = Assets.`Chevron-right`,
                    contentDescription = "more information",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(90f),
                )
            }
        }
    }

    @Composable
    private fun Background(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize()) {
            Icon(
                imageVector = Assets.`Book-open`,
                contentDescription = "background",
                modifier = Modifier
                    .size(screenSize.width / 2)
                    .align(Alignment.BottomEnd)
                    .rotate(-30f)
                    .offset(y = (-40).dp)
            )
        }
    }
}