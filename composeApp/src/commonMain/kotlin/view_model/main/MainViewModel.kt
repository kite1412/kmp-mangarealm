package view_model.main

import Cache
import Libs
import Libs.mangaDex
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.stack.mutableStateStackOf
import io.github.irgaly.kottage.KottageStorage
import kotlinx.coroutines.launch
import model.MangaStatus
import model.toMangaList
import util.retry
import view_model.ChapterNavigator
import view_model.DetailNavigator
import view_model.SharedViewModel
import view_model.main.state.DiscoveryState
import view_model.main.state.HomeState
import view_model.main.state.UserListState

class MainViewModel(
    override val sharedViewModel: SharedViewModel,
    mangaDex: MangaDex = Libs.mangaDex,
    kottageStorage: KottageStorage = Libs.kottageStorage,
    cache: Cache = Libs.cache
) : ViewModel(), DetailNavigator, ChapterNavigator {
    val menuStack = mutableStateStackOf(Menu.HOME)
    val currentPage by derivedStateOf { menuStack.lastItemOrNull }
    var undoEdgeToEdge by mutableStateOf(false)
    var hideBottomBar by mutableStateOf(false)

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

    fun pushMenu(menu: Menu) = if (menu != Menu.HOME) menuStack.push(menu)
        else menuStack.replaceAll(Menu.HOME)

    fun init() {
        homeState.initLatestUpdatesData()
        initMangaStatus()
    }

    private fun initMangaStatus() {
        viewModelScope.launch {
            val res = retry(
                count = 3,
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
                        count = 3,
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
    HOME, FEED, DISCOVERY, USER_LIST
}