package view_model.main

import Assets
import Cache
import Libs
import Libs.mangaDex
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import assets.`Book-close`
import assets.Home
import assets.Search
import cafe.adriel.voyager.core.stack.mutableStateStackOf
import io.github.irgaly.kottage.KottageStorage
import kotlinx.coroutines.launch
import model.MangaStatus
import model.Route
import model.toMangaList
import util.APP_BAR_HEIGHT
import util.retry
import view_model.ChapterNavigator
import view_model.CustomListNavigator
import view_model.DetailNavigator
import view_model.SettingsNavigator
import view_model.SharedViewModel
import view_model.main.state.DiscoveryState
import view_model.main.state.HomeState
import view_model.main.state.UserListState

val bottomBarTotalHeight = APP_BAR_HEIGHT + 8.dp

class MainViewModel(
    override val sharedViewModel: SharedViewModel,
    mangaDex: MangaDex = Libs.mangaDex,
    kottageStorage: KottageStorage = Libs.kottageStorage,
    cache: Cache = Libs.cache
) : ViewModel(), DetailNavigator, ChapterNavigator, CustomListNavigator, SettingsNavigator {
    val routes = mapOf(
        Menu.HOME to Route(
            name = "Home",
            icon = Assets.Home
        ),
        Menu.DISCOVERY to Route(
            name = "Discovery",
            icon = Assets.Search
        ),
        Menu.USER_LIST to Route(
            name = "My List",
            icon = Assets.`Book-close`
        ),
    )
    val menuStack = mutableStateStackOf(routes[Menu.HOME])
    val currentPage by derivedStateOf { menuStack.lastItemOrNull ?: routes[Menu.HOME]!! }
    var undoEdgeToEdge by mutableStateOf(false)
    var hideNavigationBar by mutableStateOf(false)

    val discoveryState = DiscoveryState(
        vm = this,
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

    fun pushMenu(route: Route) = if (route != routes[Menu.HOME]!!) menuStack.push(route)
        else menuStack.replaceAll(routes[Menu.HOME]!!)

    fun init() {
        homeState.initLatestUpdatesData()
        initMangaStatus()
    }

    private fun initMangaStatus() {
        viewModelScope.launch {
            val res = retry(
                maxAttempts = 3,
                predicate = { it == null || it.result == "error" }
            ) {
                mangaDex.getMangaByStatus()
            }
            if (res?.statuses != null) {
                if (res.statuses.isNotEmpty()) {
                    val temp = res.statuses.map { manga ->
                        manga.key
                    }
                    val mangaIds = generateArrayQueryParam(
                        name = "ids[]",
                        values = temp
                    )
                    val queries = generateQuery(mapOf(Pair("includes[]", "cover_art")), mangaIds)
                    val mangaList = retry(
                        maxAttempts = 3,
                        predicate = { it == null || it.result == "error" }
                    ) {
                        mangaDex.getManga(queries)
                    }
                    mangaList?.let {
                        it.toMangaList().map { m ->
                            res.statuses[m.data.id]?.let { s ->
                                m.status = MangaStatus.toStatus(s)
                            }
                            m
                        }.forEach { m ->
                            sharedViewModel.mangaStatus[m.status]!!.add(m)
                            sharedViewModel.mangaStatus[MangaStatus.All]!!.add(m)
                        }
                    }
                }
            }
        }
    }
}

enum class Menu {
    HOME, DISCOVERY, USER_LIST
}