package view_model.main

import Cache
import Libs
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import cafe.adriel.voyager.core.stack.mutableStateStackOf
import cafe.adriel.voyager.navigator.Navigator
import io.github.irgaly.kottage.KottageStorage
import model.Manga
import view_model.ChapterNavigator
import view_model.DetailNavigator
import view_model.main.state.DiscoveryState
import view_model.main.state.HomeState
import view_model.main.state.UserListState

class MainViewModel(
    mangaDex: MangaDex = Libs.mangaDex,
    kottageStorage: KottageStorage = Libs.kottageStorage,
    private val cache: Cache = Libs.cache
) : ViewModel(), DetailNavigator, ChapterNavigator {
    val menuStack = mutableStateStackOf(Menu.HOME)
    val currentPage by derivedStateOf { menuStack.lastItemOrNull }
    var undoEdgeToEdge by mutableStateOf(false)
    var hideBottomBar by mutableStateOf(false)

    val discoveryState = DiscoveryState(
        mangaDex = mangaDex,
        cache = cache,
        scope = viewModelScope,
        kottageStorage = kottageStorage
    )

    val homeState = HomeState(
        vm = this,
        mangaDex = mangaDex,
        cache = cache,
        scope = viewModelScope,
        kottageStorage = kottageStorage
    )

    val userListState = UserListState(this)

    fun popMenu() { menuStack.pop() }

    fun pushMenu(menu: Menu) = if (menu != Menu.HOME) menuStack.push(menu)
        else menuStack.replaceAll(Menu.HOME)

    fun init() {
        homeState.initLatestUpdatesData()
        homeState.initContinueReadingData()
    }

    fun navigateToDetailScreen(nav: Navigator, manga: Manga) {
        val m = cache.mangaStatus[manga.data.id] ?: manga
        navigateToDetail(nav, m)
    }
}

enum class Menu {
    HOME, FEED, DISCOVERY, USER_LIST
}