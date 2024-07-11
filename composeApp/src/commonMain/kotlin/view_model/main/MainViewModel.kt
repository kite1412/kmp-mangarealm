package view_model.main

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.Status
import api.mangadex.util.constructQuery
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import api.mangadex.util.getCoverUrl
import cafe.adriel.voyager.navigator.Navigator
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
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
    var currentPage by mutableStateOf(Page.MAIN)
    val discoveryState = DiscoveryState(mangaDex, cache, viewModelScope)

    var mangaTagsLabelHeight = mutableStateOf(0)

    var undoEdgeToEdge by mutableStateOf(false)

    val enableAutoSlide = derivedStateOf {
        latestUpdatesData.isNotEmpty() && latestUpdatesPainter.isNotEmpty() && latestUpdatesPainter[0] != null
    }

    val sessionSize = 10
    var latestUpdatesBarPage = 0
    var adjustLatestUpdatesBar = false

    val latestUpdatesData = mutableStateListOf<Data<MangaAttributes>>()
    val continueReadingData = mutableStateListOf<Data<MangaAttributes>>()

    private var _username = mutableStateOf("")
    val username = _username

    val romComManga = mutableStateListOf<Data<MangaAttributes>>()
    val advComManga = mutableStateListOf<Data<MangaAttributes>>()
    val psyMysManga = mutableStateListOf<Data<MangaAttributes>>()

    private var isRomComComplete = false
    private var isAdvComComplete = false
    private var isPsyMysComplete = false

    val latestUpdatesPainter = mutableStateListOf<Painter?>()
    val continueReadingPainter = mutableStateListOf<Painter?>()

    val romComPainter = mutableStateListOf<Painter?>()
    val advComPainter = mutableStateListOf<Painter?>()
    val psyMysPainter = mutableStateListOf<Painter?>()

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
                predicate = { latestUpdatesData.isEmpty() }
            ) {
                val includes = generateArrayQueryParam(
                    name = "includes[]",
                    values = listOf("cover_art")
                )
                val queries = generateQuery(mapOf("limit" to sessionSize), includes)
                val res = mangaDex.getManga(queries)
                if (res != null) {
                    res.data.forEach {
                        latestUpdatesData.add(it)
                        latestUpdatesPainter.add(null)
                    }
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
                predicate = { continueReadingData.isEmpty() }
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
                        mangaList?.data?.let {
                            it.forEach { d ->
                                continueReadingData.add(d)
                                continueReadingPainter.add(null)
                                cache.mangaStatus[d.id] = Manga(
                                    data = d,
                                    status = MangaStatus.Reading
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun syncReadingStatus() {
        val reading = cache.mangaStatus.filter { it.value.status == MangaStatus.Reading }
        if (SharedObject.detailCover != null && reading.size != continueReadingData.size) {
            if (continueReadingData.size < reading.size) continueReadingPainter.add(SharedObject.detailCover)
                else continueReadingPainter.remove(SharedObject.detailCover)
            continueReadingData.clear()
            continueReadingData.addAll(reading.values.map { it.data })
        }
    }

    private suspend fun fetchByTags(
        tags: List<String>,
        onSuccess: (List<Data<MangaAttributes>>) -> Unit,
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
                onSuccess(res.data)
            }
        }
    }

    suspend fun fetchMangaByTags(tags: Map<String, String>) {
        fetchByTags(
            tags = listOf(tags[ROMANCE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = romComManga::addAll,
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = advComManga::addAll,
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!, tags[ROMANCE_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!),
            onSuccess = psyMysManga::addAll,
            excludedTags = listOf(tags[COMEDY_TAG]!!)
        )
    }

    @Composable
    private fun initRomCom() {
        if (romComManga.isNotEmpty() && !isRomComComplete) {
            for (i in 0 until romComManga.size) romComPainter.add(null)
            romComManga.forEachIndexed { i, d ->
                AutoSizeBox(getCoverUrl(d)) {action ->
                    when (action) {
                        is ImageAction.Success -> {
                            romComPainter[i] = rememberImageSuccessPainter(action)
                        }
                        else -> Unit
                    }
                }
            }
            isRomComComplete = true
        }
    }

    @Composable
    private fun initAdvCom() {
        if (advComManga.isNotEmpty() && !isAdvComComplete) {
            for (i in 0 until advComManga.size) advComPainter.add(null)
            advComManga.forEachIndexed { i, d ->
                AutoSizeBox(getCoverUrl(d)) {action ->
                    when (action) {
                        is ImageAction.Success -> {
                            advComPainter[i] = rememberImageSuccessPainter(action)
                        }
                        else -> Unit
                    }
                }
            }
            isAdvComComplete = true
        }
    }

    @Composable
    private fun initPsyMys() {
        if (psyMysManga.isNotEmpty() && !isPsyMysComplete) {
            for (i in 0 until psyMysManga.size) psyMysPainter.add(null)
            psyMysManga.forEachIndexed { i, d ->
                AutoSizeBox(getCoverUrl(d)) {action ->
                    when (action) {
                        is ImageAction.Success -> {
                            psyMysPainter[i] = rememberImageSuccessPainter(action)
                        }
                        else -> Unit
                    }
                }
            }
            isPsyMysComplete = true
        }
    }

    @Composable
    fun initMangaTagsPainter() {
        initRomCom()
        initAdvCom()
        initPsyMys()
    }

    fun navigateToDetailScreen(nav: Navigator, painter: Painter?, manga: Manga) {
        val m = cache.mangaStatus[manga.data.id] ?: manga
        navigateToDetail(nav, painter, m)
    }
}

enum class Page {
    MAIN, FEED, DISCOVERY, USER_LIST
}