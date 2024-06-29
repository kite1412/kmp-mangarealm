package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.util.getTags
import api.mangadex.util.getTitle
import cafe.adriel.voyager.core.screen.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import model.Manga
import screenSize
import util.BLUR_TINT
import viewmodel.DetailViewModel

class DetailScreen(
    private val vm: DetailViewModel
) : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    CoverArtDisplay(vm.manga)
                }
            }
        }
    }

    @Composable
    private fun CoverArtDisplay(
        manga: Manga,
        modifier: Modifier = Modifier
    ) {
        val hazeState = remember { HazeState() }
        val totalHeight = screenSize.height / 3
        val backgroundHeight = totalHeight / 1.3f
        val coverArtHeight = totalHeight / 1.5f
        val coverArtWidth = (coverArtHeight * 2) / 3
        Box(
            modifier = modifier
                .height(totalHeight)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.haze(hazeState)) {
                BrowseImageNullable(
                    painter = manga.painter,
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
                    .width(screenSize.width - (coverArtWidth + 16.dp))
                    .padding(top = 16.dp, start = 12.dp, end = 4.dp)
            ) {
                Text(
                    getTitle(manga.data.attributes.title),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    getTags(manga.data),
                    maxLines = 2,
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
            BrowseImageNullable(
                painter = manga.painter,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
                    .width(coverArtWidth)
                    .height(coverArtHeight)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}