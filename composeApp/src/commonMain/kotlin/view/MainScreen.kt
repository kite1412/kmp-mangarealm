package view

import LocalMainViewModel
import LocalScreenSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import model.ScreenSize
import util.undoEdgeToEdge
import view.adaptive.AdaptiveNavigationBar
import view_model.main.Menu

class MainScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val vm = LocalMainViewModel.current
        LifecycleEffectOnce {
            vm.init()
        }
        if (vm.undoEdgeToEdge) undoEdgeToEdge()
        val nav = LocalNavigator.currentOrThrow
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            val routes = vm.routes
            AdaptiveNavigationBar(
                routes = routes.values.toList(),
                selectedRoute = vm.currentPage,
                onRouteSelected = {
                    vm.pushMenu(it)
                },
                showNavigationBar = !vm.hideBottomBar
            ) {
                BackHandler(enabled = vm.menuStack.size > 1) { vm.popMenu() }
                CompositionLocalProvider(LocalScreenSize provides ScreenSize(maxHeight, maxWidth)) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when(vm.currentPage) {
                            routes[Menu.DISCOVERY] -> Discovery(vm)
                            routes[Menu.USER_LIST] -> UserList(vm)
                            else -> Home(vm, nav)
                        }
                        if (vm.discoveryState.showDeletionWarning) DeletionWarning(vm.discoveryState)
                    }
                }
            }
//            BackHandler(enabled = vm.menuStack.size > 1) { vm.popMenu() }
//            Box(
//                modifier = Modifier.fillMaxSize()
//            ) {
//                when(vm.currentPage) {
//                    Menu.DISCOVERY -> Discovery(vm)
//                    Menu.USER_LIST -> UserList(vm)
//                    else -> Home(vm, nav)
//                }
//                if (!vm.hideBottomBar) BottomAppBar(
//                    page = vm.currentPage!!,
//                    onPageChange = vm::pushMenu,
//                    modifier = Modifier.align(Alignment.BottomCenter)
//                )
//                if (vm.discoveryState.showDeletionWarning) DeletionWarning(vm.discoveryState)
//            }
        }
    }

//    @Composable
//    private fun BottomAppBar(
//        page: Menu,
//        onPageChange: (Menu) -> Unit,
//        modifier: Modifier = Modifier
//    ) {
//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .height(APP_BAR_HEIGHT)
//                .padding(horizontal = 8.dp)
//                .offset(y = (-8).dp)
//                .clip(RoundedCornerShape(15.dp))
//                .background(MaterialTheme.colors.onBackground)
//                .clickable(enabled = false) {},
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            BottomAppBarIcon(
//                page = Menu.HOME,
//                imageVector = Assets.Home,
//                contentDescription = "main page",
//                selected = page == Menu.HOME,
//                onClick = onPageChange
//            )
//            BottomAppBarIcon(
//                page = Menu.DISCOVERY,
//                imageVector = Assets.Search,
//                contentDescription = "discovery page",
//                selected = page == Menu.DISCOVERY,
//                onClick = onPageChange
//            )
//            BottomAppBarIcon(
//                page = Menu.USER_LIST,
//                imageVector = Assets.`Book-close`,
//                contentDescription = "your list page",
//                selected = page == Menu.USER_LIST,
//                onClick = onPageChange
//            )
//        }
//    }
//
//    @Composable
//    fun BottomAppBarIcon(
//        page: Menu,
//        imageVector: ImageVector,
//        contentDescription: String,
//        selected: Boolean,
//        onClick: (Menu) -> Unit,
//        modifier: Modifier = Modifier
//    ) {
//        val isDarkMode by LocalSharedViewModel.current.appSettings.isDarkMode
//
//        val unselectedButton = Color(0xFFD1C5B4)
//        val selectedButton = Color(0xFF322C00)
//        Icon(
//            imageVector = imageVector,
//            contentDescription = contentDescription,
//            tint = if (!selected) if (isDarkMode) unselectedButton else Color(0xFFE2D6C6)
//                    else selectedButton,
//            modifier = modifier
//                .size(32.dp)
//                .clickable(
//                    indication = null,
//                    interactionSource = MutableInteractionSource()
//                ) { onClick(page) }
//        )
//    }
}