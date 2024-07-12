package view

import Assets
import LocalMainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import api.mangadex.util.getCoverUrl
import assets.`Book-close`
import assets.Clipboard
import assets.Home
import assets.Search
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import theme.selectedButton
import theme.unselectedButton
import util.APP_BAR_HEIGHT
import util.undoEdgeToEdge
import view_model.main.Page

class MainScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val vm = LocalMainViewModel.current
        LifecycleEffectOnce {
            vm.init()
        }
        if (vm.undoEdgeToEdge) undoEdgeToEdge()
        vm.syncReadingStatus()
        val nav = LocalNavigator.currentOrThrow
        val latestUpdatesData = vm.latestUpdates
        if (latestUpdatesData.isNotEmpty()) {
            PaintersLoader(
                urls = latestUpdatesData.map { getCoverUrl(it.data) },
            ) { i, p ->
                vm.latestUpdates[i] = latestUpdatesData[i].copy(painter = p)
            }
        }
        val continueReadingData = vm.continueReading
        if (continueReadingData.isNotEmpty()) PaintersLoader(
            urls = continueReadingData.map { getCoverUrl(it.data) },
        ) { i, p ->
            vm.continueReading[i] = continueReadingData[i].copy(painter = p)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when(vm.currentPage) {
                    Page.DISCOVERY -> Discovery(vm)
                    else -> Home(vm, nav)
                }
                BottomAppBar(
                    page = vm.currentPage,
                    onPageChange = {
                        vm.currentPage = it
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    @Composable
    private fun BottomAppBar(
        page: Page,
        onPageChange: (Page) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(APP_BAR_HEIGHT)
                .padding(horizontal = 8.dp)
                .offset(y = (-8).dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colors.onBackground)
                .clickable(enabled = false) {},
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAppBarIcon(
                page = Page.MAIN,
                imageVector = Assets.Home,
                contentDescription = "main page",
                selected = page == Page.MAIN,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Page.FEED,
                imageVector = Assets.Clipboard,
                contentDescription = "feed page",
                selected = page == Page.FEED,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Page.DISCOVERY,
                imageVector = Assets.Search,
                contentDescription = "discovery page",
                selected = page == Page.DISCOVERY,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Page.USER_LIST,
                imageVector = Assets.`Book-close`,
                contentDescription = "your list page",
                selected = page == Page.USER_LIST,
                onClick = onPageChange
            )
        }
    }

    @Composable
    fun BottomAppBarIcon(
        page: Page,
        imageVector: ImageVector,
        contentDescription: String,
        selected: Boolean,
        onClick: (Page) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = if (selected) MaterialTheme.colors.selectedButton
            else MaterialTheme.colors.unselectedButton,
            modifier = modifier
                .size(32.dp)
                .clickable{ onClick(page) }
        )
    }
}