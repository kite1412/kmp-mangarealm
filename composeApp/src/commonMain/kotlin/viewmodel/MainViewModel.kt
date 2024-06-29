package viewmodel

import Libs
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG
import view.DetailScreen

class MainViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage
) : ViewModel() {
    private var _currentPage = mutableStateOf(Page.MAIN)
    val currentPage = _currentPage

    var mangaTagsLabelHeight = mutableStateOf(0)

    private var initLatestUpdatesPainter = false
    private var initContinueReadingPainter = false

    private var isLatestUpdatesPainterComplete = mutableStateOf(false)
    private var isContinueReadingPainterComplete = mutableStateOf(false)

    val enableAutoSlide = derivedStateOf {
        latestUpdatesData.isNotEmpty() && latestUpdatesPainter.isNotEmpty() && latestUpdatesPainter[0] != null
    }

    val sessionSize = 10

    val latestUpdatesData = mutableStateListOf<Data<MangaAttributes>>()
    val continueReadingData = mutableStateListOf<Data<MangaAttributes>>()

    private var _username = mutableStateOf("")
    val username = _username

    val romComManga = mutableStateListOf<Data<MangaAttributes>>()
    val advComManga = mutableStateListOf<Data<MangaAttributes>>()
    val psyMysManga = mutableStateListOf<Data<MangaAttributes>>()

    private var isRomComDataComplete = mutableStateOf(false)
    private var isAdvComDataComplete = mutableStateOf(false)
    private var isPsyMysDataComplete = mutableStateOf(false)
    private var isRomComComplete = false
    private var isAdvComComplete = false
    private var isPsyMysComplete = false

    val latestUpdatesPainter = mutableStateListOf<Painter?>()
    val continueReadingPainter = mutableStateListOf<Painter?>()

    val romComPainter = mutableStateListOf<Painter?>()
    val advComPainter = mutableStateListOf<Painter?>()
    val psyMysPainter = mutableStateListOf<Painter?>()

    fun setPage(page: Page) {
        _currentPage.value = page
    }

    suspend fun updateUsername() {
        val username = kottageStorage.get<String>(KottageConst.USERNAME)
        _username.value = username
    }

    @Composable
    fun init() {
        initLatestUpdates()
        initContinueReading()
    }

    private fun initLatestUpdatesData() {
        viewModelScope.launch {
            if (latestUpdatesData.isEmpty()) {
                val includes = generateArrayQueryParam(
                    name = "includes[]",
                    values = listOf("cover_art")
                )
                val queries = generateQuery(mapOf("limit" to sessionSize), includes)
                latestUpdatesData.addAll(mangaDex.getManga(queries)?.data ?: listOf())
            }
        }
    }

    @Composable
    private fun initLatestUpdates() {
        initLatestUpdatesData()
        if (latestUpdatesData.isNotEmpty() && !initLatestUpdatesPainter) {
            for (i in 0 until latestUpdatesData.size) latestUpdatesPainter.add(null)
            initLatestUpdatesPainter = true
        }
        for ((i, d) in latestUpdatesData.withIndex()) {
            AutoSizeBox(getCoverUrl(d)) {action ->
                when (action) {
                    is ImageAction.Success -> {
                        latestUpdatesPainter[i] = rememberImageSuccessPainter(action)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initContinueReadingData() {
        viewModelScope.launch {
            if (continueReadingData.isEmpty()) {
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
                        mangaList?.data?.let { continueReadingData.addAll(it) }
                    }
                }
            }
        }
    }

    @Composable
    fun initContinueReading() {
        initContinueReadingData()
        if (continueReadingData.isNotEmpty() && !initContinueReadingPainter) {
            for (i in 0 until continueReadingData.size) continueReadingPainter.add(null)
            initContinueReadingPainter = true
        }
        for ((i, d) in continueReadingData.withIndex()) {
            AutoSizeBox(getCoverUrl(d)) {action ->
                when (action) {
                    is ImageAction.Success -> {
                        continueReadingPainter[i] = rememberImageSuccessPainter(action)
                    }
                    else -> Unit
                }
            }
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
            onSuccess = {
                romComManga.addAll(it)
                isRomComDataComplete.value = true
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!),
            onSuccess = {
                advComManga.addAll(it)
                isAdvComDataComplete.value = true
            },
            excludedTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!)
        )
        fetchByTags(
            tags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!),
            onSuccess = {
                psyMysManga.addAll(it)
                isPsyMysDataComplete.value = true
            },
            excludedTags = listOf(tags[COMEDY_TAG]!!)
        )
    }

    @Composable
    private fun initRomCom() {
        if (isRomComDataComplete.value && !isRomComComplete) {
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
        if (isAdvComDataComplete.value && !isAdvComComplete) {
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
        if (isPsyMysDataComplete.value && !isPsyMysComplete) {
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

    fun navigateToDetail(nav: Navigator, manga: Manga) {
        if (manga.painter != null) nav.push(DetailScreen(vm = DetailViewModel(manga)))
    }
}

enum class Page {
    MAIN, FEED, DISCOVERY, USER_LIST
}