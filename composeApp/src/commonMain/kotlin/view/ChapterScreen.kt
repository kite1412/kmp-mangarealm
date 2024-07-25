package view

import Assets
import LocalScreenSize
import LocalSharedViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assets.Cross
import assets.`Settings-adjust-solid`
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.white_textured_concrete
import model.Chapter
import model.session.SessionState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.APP_BAR_HEIGHT
import util.ASCENDING
import util.DESCENDING
import util.edgeToEdge
import util.isInDarkMode
import util.mapLanguage
import util.session_handler.ChapterSessionHandler
import util.swipeToPop
import view_model.ChapterScreenModel

class ChapterScreen : Screen {
    @Composable
    override fun Content() {
        edgeToEdge()
        val sharedViewModel = LocalSharedViewModel.current
        val sm = rememberScreenModel { ChapterScreenModel(sharedViewModel) }
        val nav = LocalNavigator.currentOrThrow
        val navBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val statusBarsHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val appBarHeight = APP_BAR_HEIGHT + statusBarsHeight
        // TODO test using BackHandler to handle back press when settings are open
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarsHeight)
                .swipeToPop(nav)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ChapterList(
                    sm = sm,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = statusBarsHeight)
                        .align(Alignment.Center)
                )
                TopBar(appBarHeight)
                if (!sm.showWarning) ChapterSettings(
                    sm = sm,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 16.dp)
                )
                Warning(
                    message = "Loading read markers...",
                    height = APP_BAR_HEIGHT,
                    show = sm.fetchingChaptersReadMarker,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    @Composable
    private fun ChapterList(
        sm: ChapterScreenModel,
        modifier: Modifier = Modifier
    ) {
        val session by sm.chapterSession
        val nav = LocalNavigator.currentOrThrow
        Box(modifier = modifier.fillMaxSize()) {
            if (session == null) if (!sm.showWarning) LoadingIndicator(Modifier.align(Alignment.Center)) {
                Text("Loading chapters...", color = Color.White)
            } else EmptyList(
                message = "No available chapters for this manga",
                modifier = Modifier.align(Alignment.Center)
            ) else {
                when(session!!.state.value) {
                    SessionState.IDLE, SessionState.FETCHING -> LoadingIndicator(Modifier.align(Alignment.Center)) {
                        Text("Loading chapters...", color = Color.White)
                    }
                    SessionState.ACTIVE -> if (session!!.data.isNotEmpty()) SessionPagerColumn(
                        session = session!!,
                        handler = ChapterSessionHandler(session!!),
                        state = sm.chapterListState,
                        contentPadding = PaddingValues(
                            top = APP_BAR_HEIGHT + 4.dp,
                            bottom = APP_BAR_HEIGHT + 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val chapter = session!!.data[it]
                        ChapterBar(chapter) { _ ->
                            sm.navigateToReader(nav, model.ChapterList(it, session!!.data))
                        }
                    } else EmptyList("No chapters found", Modifier.align(Alignment.Center))
                }
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
    private fun ChapterSettings(
        sm: ChapterScreenModel,
        modifier: Modifier = Modifier
    ) {
        val screenSize = LocalScreenSize.current
        val settingHeight = screenSize.height / 2
        AnimatedVisibility(
            visible = sm.showSettings,
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(settingHeight)
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(Res.drawable.white_textured_concrete),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Box {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                "Search Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Italic
                            )
                            IconButton(onClick = sm::onSettingClick) {
                                Icon(
                                    imageVector = Assets.Cross,
                                    contentDescription = "next page",
                                    tint = Color.Red,
                                )
                            }
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                        ) {
                            item {
                                LanguageSelection(sm)
                            }
                            item {
                                OrderSelection(sm)
                            }
                            item {
                                Spacer(Modifier.height(26.dp))
                            }
                        }
                    }
                    ApplySettingsButton(
                        enabled = sm.applyEnabled,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 18.dp, bottom = 24.dp),
                        onClick = sm::onApplySettingsClick
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !sm.showSettings,
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .size(APP_BAR_HEIGHT)
                    .clip(CircleShape)
                    .clickable(onClick = sm::onSettingClick)
            ) {
                Image(
                    painter = painterResource(Res.drawable.white_textured_concrete),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.8f,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Assets.`Settings-adjust-solid`,
                    contentDescription = "settings",
                    tint = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    @Composable
    private fun ApplySettingsButton(
        enabled: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier.clickable(enabled, onClick = onClick)
        ) {
            Text(
                "Apply",
                color = if (enabled) Color(0, 200, 0) else Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun LanguageSelection(
        sm: ChapterScreenModel,
        modifier: Modifier = Modifier
    ) {
        if (sm.availableLanguages.isNotEmpty()) Selection("Language") {
            sm.availableLanguages.forEach {
                SelectionBar(
                    label = mapLanguage(it),
                    selected = sm.languageSetting == it,
                    onClick = { sm.languageSetting = it }
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun OrderSelection(
        sm: ChapterScreenModel,
        modifier: Modifier = Modifier
    ) {
        Selection("Order") {
            SelectionBar("Ascending", sm.orderSetting == ASCENDING) { sm.orderSetting = ASCENDING }
            SelectionBar("Descending", sm.orderSetting == DESCENDING) { sm.orderSetting = DESCENDING }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun Selection(
        label: String,
        modifier: Modifier = Modifier,
        selections: @Composable FlowRowScope.() -> Unit
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) { selections() }
        }
    }

    @Composable
    private fun SelectionBar(
        label: String,
        selected: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val clip = RoundedCornerShape(6.dp)
        val outer = if (!selected) Modifier.border(
            width = 1.dp,
            color = Color.DarkGray,
            shape = clip
        )
            else Modifier
        Box(
            modifier = modifier
                .clip(clip)
                .clickable(onClick = onClick)
                .background(if (selected) Color.DarkGray else Color.Transparent)
                .then(outer)
        ) {
            Text(
                label,
                color = if (selected) Color.White else Color.DarkGray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ChapterBar(
        chapter: Chapter,
        modifier: Modifier = Modifier,
        onClick: (Chapter) -> Unit
    ) {
        Card(
            backgroundColor = if (chapter.isRead.value)
                if (!isInDarkMode()) Color(0xFFA39C8E) else Color(0xFF0A0A0A)
                    else MaterialTheme.colors.background ,
            elevation = 6.dp,
            shape = RoundedCornerShape(4.dp),
            modifier = modifier.fillMaxWidth(),
            onClick = { onClick(chapter) }
        ) {
            val c = chapter()
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                Row {
                    Text(
                        "Chapter ${c.attributes.chapter ?: ""}",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.primary
                    )
                    if (!c.attributes.title.isNullOrEmpty()) Row {
                        Text(
                            ": ",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            "${c.attributes.title}",
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
                        .background(MaterialTheme.colors.primary)
                )
            }
        }
    }
}