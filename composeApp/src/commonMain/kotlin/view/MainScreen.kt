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
                showNavigationBar = !vm.hideNavigationBar
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
        }
    }
}