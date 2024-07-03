package view

import Assets
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes
import assets.`Chevron-right`
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.white_textured_concrete
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.APP_BAR_HEIGHT
import util.swipeToPop
import viewmodel.ChapterScreenModel

class ChapterScreen : Screen {
    @Composable
    override fun Content() {
        val sm = rememberScreenModel { ChapterScreenModel() }
        val nav = LocalNavigator.currentOrThrow
        val navBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val appBarHeight = APP_BAR_HEIGHT + statusBarsHeight
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarsHeight)
                .swipeToPop(nav)
        ) {
            Box {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp, top = statusBarsHeight)
                ) {
                    item {
                        Spacer(Modifier.height(APP_BAR_HEIGHT))
                    }
                    items(sm.chapters) {
                        ChapterBar(it, modifier = Modifier.padding(horizontal = 8.dp)) {

                        }
                    }
                    item {
                        Spacer(Modifier.height(APP_BAR_HEIGHT))
                    }
                }
                TopBar(appBarHeight)
                BottomBar(
                    sm = sm,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun TopBar(
        height: Dp,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
        ) {
            Image(
                painter = painterResource(Res.drawable.white_textured_concrete),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alpha = 0.8f,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                "Chapter List",
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun BottomBar(
        sm: ChapterScreenModel,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
        ) {
            Image(
                painter = painterResource(Res.drawable.white_textured_concrete),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alpha = 0.8f,
            )
           Box(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(horizontal = 16.dp)
           ) {
               Text(
                   "${sm.currentPage}/${sm.totalPages}",
                   fontSize = 18.sp,
                   fontWeight = FontWeight.Medium,
                   modifier = Modifier.align(Alignment.Center)
               )
               if (sm.currentPage < sm.totalPages) IconButton(
                   onClick = {},
                   modifier = Modifier.align(Alignment.CenterEnd)
               ) {
                   Icon(
                       imageVector = Assets.`Chevron-right`,
                       contentDescription = "next page",
                       tint = Color.Black
                   )
               }
               if (sm.currentPage > 1) IconButton(
                   onClick = {},
                   modifier = Modifier.align(Alignment.CenterStart),
               ) {
                   Icon(
                       imageVector = Assets.`Chevron-right`,
                       contentDescription = "next page",
                       tint = Color.Black,
                       modifier = Modifier.rotate(180f)
                   )
               }
           }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ChapterBar(
        chapter: Data<ChapterAttributes>,
        modifier: Modifier = Modifier,
        onClick: (Data<ChapterAttributes>) -> Unit
    ) {
        if (chapter.attributes.chapter != null) Card(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 6.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.fillMaxWidth(),
            onClick = { onClick(chapter) }
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                Row {
                    Text(
                        "Chapter ${chapter.attributes.chapter}",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.primary
                    )
                    if (!chapter.attributes.title.isNullOrEmpty()) Row {
                        Text(
                            ": ",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            "${chapter.attributes.title}",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.primary
                        )
                    }

                }
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 4.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colors.onBackground)
                )
            }
        }
    }
}