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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import assets.`Book-close`
import assets.Clipboard
import assets.Home
import assets.Search
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import theme.selectedButton
import theme.unselectedButton
import util.APP_BAR_HEIGHT
import util.undoEdgeToEdge
import view_model.main.Menu

class MainScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val vm = LocalMainViewModel.current
        LifecycleEffectOnce {
            vm.init()
        }
        SideEffect {
            vm.homeState.syncReadingStatus()
        }
        if (vm.undoEdgeToEdge) undoEdgeToEdge()
        val nav = LocalNavigator.currentOrThrow
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            BackHandler(enabled = vm.menuStack.size > 1) { vm.popMenu() }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when(vm.currentPage) {
                    Menu.DISCOVERY -> Discovery(vm)
                    Menu.USER_LIST -> UserList(vm)
                    else -> Home(vm, nav)
                }
                if (!vm.hideBottomBar) BottomAppBar(
                    page = vm.currentPage!!,
                    onPageChange = vm::pushMenu,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                if (vm.discoveryState.showDeletionWarning) DeletionWarning(vm.discoveryState)
            }
        }
    }

    @Composable
    private fun BottomAppBar(
        page: Menu,
        onPageChange: (Menu) -> Unit,
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
                page = Menu.HOME,
                imageVector = Assets.Home,
                contentDescription = "main page",
                selected = page == Menu.HOME,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Menu.FEED,
                imageVector = Assets.Clipboard,
                contentDescription = "feed page",
                selected = page == Menu.FEED,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Menu.DISCOVERY,
                imageVector = Assets.Search,
                contentDescription = "discovery page",
                selected = page == Menu.DISCOVERY,
                onClick = onPageChange
            )
            BottomAppBarIcon(
                page = Menu.USER_LIST,
                imageVector = Assets.`Book-close`,
                contentDescription = "your list page",
                selected = page == Menu.USER_LIST,
                onClick = onPageChange
            )
        }
    }

    @Composable
    fun BottomAppBarIcon(
        page: Menu,
        imageVector: ImageVector,
        contentDescription: String,
        selected: Boolean,
        onClick: (Menu) -> Unit,
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