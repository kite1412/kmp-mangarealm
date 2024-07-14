package view_model.main

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.Status
import api.mangadex.util.constructQuery
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.stack.mutableStateStackOf
import cafe.adriel.voyager.navigator.Navigator
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.toMangaList
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG
import util.retry
import view_model.DetailNavigator
import view_model.main.state.DiscoveryState

class MainViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage,
    private val cache: Cache = Libs.cache
) : ViewModel(), DetailNavigator {
    val menuStack = mutableStateStackOf(Menu.HOME)
    val currentPage by derivedStateOf { menuStack.lastItemOrNull }

    val discoveryState = DiscoveryState(
        mangaDex = mangaDex,
        cache = cache,
        scope = viewModelScope,
        kottageStorage = kottageStorage
    )

    var undoEdgeToEdge by mutableStateOf(false)

    val enableAutoSlide = derivedStateOf {
        latestUpdates.isNotEmpty() && latestUpdates[0].painter != null
    }

    val sessionSize = 10
    var latestUpdatesBarPage = 0
    var adjustLatestUpdatesBar = false

    val latestUpdates = mutableStateListOf<Manga>()
    val continueReading = mutableStateListOf<Manga>()

    private var _username = mutableStateOf("")
    val username = _username

    val romCom = mutableStateListOf<Manga>()
    val advCom = mutableStateListOf<Manga>()
    val psyMys = mutableStateListOf<Manga>()

    fun popMenu() { menuStack.pop() }

    fun pushMenu(menu: Menu) = if (menu != Menu.HOME) menuStack.push(menu)
        else menuStack.replaceAll(Menu.HOME)

    suspend fun updateUsername() {
        val username = kottageStorage.get<String>(KottageConst.USERNAME)
        _username.value = username
    }

    fun init() {
        initLatestUpdatesData()
        initContinueReadingData()
    }

    private fun initLatestUpdatesData() {
        viewModelScope.launch {
            retry(
                count = 3,
                predicate = { latestUpdates.isEmpty() }
            ) {
                val includes = generateArrayQueryParam(
                    name = "includes[]",
                    values = listOf("cover_art")
                )
                val queries = generateQuery(mapOf("limit" to sessionSize), includes)
                val res = mangaDex.getManga(queries)
                if (res != null) {
                    latestUpdates.addAll(res.toMangaList())
                } else {
                    // TODO handle
                }
            }
        }
    }

    private fun initContinueReadingData() {
        viewModelScope.launch {
            retry(
                count = 3,
                predicate = { continueReading.isEmpty() }
            ) {
                // TODO change to all statuses
                val res = mangaDex.getMangaByStatus(Status.READING)
                if (res?.statuses != null) {
                    if (res.statuses.isNotEmpty()) {
                        val temp = mutableListOf<String>()
                        res.statuses.forEach { manga ->
                            temp.add(manga.key)
                        }
                        val mangaIds = generateArrayQueryParam(
                            name = "ids[]",
                            values = temp
                        )
                        val queries = generateQuery(mapOf(Pair("includes[]", "cover_art")), mangaIds)
                        val mangaList = mangaDex.getManga(queries)
                        mangaList?.let {
                            val data = it.toMangaList().map { m ->
                                m.status = MangaStatus.Reading
                                m
                            }
                            continueReading.addAll(data)
                            cache.mangaStatus.putAll(data.associateBy { m -> m.data.id })
                        }
                    }
                }
            }
        }
    }

    fun syncReadingStatus() {
        val reading = cache.mangaStatus.filter { it.value.status == MangaStatus.Reading }
        if (reading.size != continueReading.size) {
            if (continueReading.size < reading.size) continueReading.add(SharedObject.detailManga)
                else continueReading.removeAll { SharedObject.detailManga.data.id == it.data.id }
        }
    }

    private suspend fun fetchByTags(
        tags: List<String>,
        onSuccess: (ListResponse<MangaAttributes>) -> Unit,
        excludedTags: List<String> = listOf()
    ) {
        val tagsParam = generateArrayQueryParam(
            name = "includedTags[]",
            values = tags
        )
        val excludedTagsParam = generateArrayQueryParam(
            name = "excludedTags[]",
            values = excludedTags
        )
        val includes = generateQuery(
            queryParams = mapOf("includes[]" to "cover_art"),
            otherParams = tagsParam
        )
        val temp = generateQuery(
            queryParams = mapOf("limit" to sessionSize),
            otherParams = includes
        )
        val queries = constructQuery(excludedTagsParam, temp)
        val res = mangaDex.getManga(queries)
        if (res != null) {
            if (res.data.isNotEmpty()) {
                onSuccess(res)
            }
        }
    }

    suspend fun fetchMangaByTags(tags: Map<String, String>) {
        fetchByTags(
            tags = listOf(tags[ROMANCE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = {
                romCom.addAll(it.toMangaList())
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = {
                advCom.addAll(it.toMangaList())
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!, tags[ROMANCE_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!),
            onSuccess = {
                psyMys.addAll(it.toMangaList())
            },
            excludedTags = listOf(tags[COMEDY_TAG]!!)
        )
    }

    fun navigateToDetailScreen(nav: Navigator, manga: Manga) {
        val m = cache.mangaStatus[manga.data.id] ?: manga
        navigateToDetail(nav, m)
    }
}

enum class Menu {
    HOME, FEED, DISCOVERY, USER_LIST
}