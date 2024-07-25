package view_model.main.state

import Cache
import Libs
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.navigator.Navigator
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.session.MangaSession
import model.session.Session
import model.session.SessionState
import model.toMangaList
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.DEFAULT_COLLECTION_SIZE
import util.ISEKAI
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG
import util.SCI_FI
import util.StatusUpdater
import util.retry
import view_model.SharedViewModel
import view_model.main.MainViewModel

class HomeState(
    private val vm: MainViewModel,
    private val scope: CoroutineScope = vm.viewModelScope,
    override val sharedViewModel:
    SharedViewModel = vm.sharedViewModel,
    override val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage,
    override val cache: Cache = Libs.cache
): StatusUpdater {
    val enableAutoSlide = derivedStateOf {
        latestUpdates.isNotEmpty() && latestUpdates[0].painter != null
    }

    val sessionSize = 10
    var latestUpdatesBarPage = 0

    val latestUpdates = mutableStateListOf<Manga>()
    val continueReading by derivedStateOf { sharedViewModel.mangaStatus[MangaStatus.Reading] ?: listOf() }

    private var _username = mutableStateOf("")
    val username = _username
    private var tags = mapOf<String, String>()
    var romComTags = listOf<String>() to listOf<String>()
    var advComTags = listOf<String>() to listOf<String>()
    var psyMysTags = listOf<String>() to listOf<String>()
    var sciTags = listOf<String>() to listOf<String>()
    var iseTags = listOf<String>() to listOf<String>()
    val session = MangaSession()
    private var sessionQueries = ""
    var showOptions by mutableStateOf(false)

    suspend fun updateUsername() {
        val username = kottageStorage.get<String>(KottageConst.USERNAME)
        _username.value = username
    }

    fun setTags(tags: Map<String, String>) {
        this.tags = tags
        psyMysTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!) to listOf(tags[COMEDY_TAG]!!)
        romComTags = listOf(tags[ROMANCE_TAG]!!, tags[COMEDY_TAG]!!) to psyMysTags.first
        advComTags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!) to (psyMysTags.first + listOf(tags[ROMANCE_TAG]!!))
        sciTags = listOf(tags[SCI_FI]!!) to listOf(tags[ROMANCE_TAG]!!)
        iseTags = listOf(tags[ISEKAI]!!, tags[ADVENTURE_TAG]!!) to listOf()
    }

    fun initLatestUpdatesData() {
        scope.launch {
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

    fun beginSession(queries: Map<String, Any>) {
        scope.launch {
            if (tags.isNotEmpty()) {
                val q = generateQuery(queries)
                val fromCache = cache.latestMangaSearch[q]
                session.clear()
                vm.hideBottomBar = true
                sessionQueries = q
                if (fromCache == null) {
                    session.init(queries)
                    val res = retry(
                        count = 3,
                        predicate = { it == null || it.errors != null }
                    ) {
                        mangaDex.getManga(q)
                    }
                    if (res != null) {
                        val data = res.toMangaList().map {
                            if (sharedViewModel.findMangaStatus(it)?.status == MangaStatus.Reading) it.copy(
                                status = MangaStatus.Reading
                            ) else it
                        }
                        session.setActive(res, data)
                        cache.latestMangaSearch[q] = MangaSession().apply { from(session) }
                    }
                } else {
                    session.from(fromCache).also {
                        it.data.forEachIndexed { i, m ->
                            val isReading = vm.sharedViewModel.mangaStatus[MangaStatus.Reading]!!.find { m2 -> m2.data.id == m.data.id } != null
                            if (isReading)
                                it.data[i] = m.copy(status = MangaStatus.Reading)
                            else if (m.status == MangaStatus.Reading && !isReading)
                                it.data[i] = m.copy(status = MangaStatus.None)
                        }
                    }
                }
            }
        }
    }

    fun clearSession() { session.clear() }

    fun setIncludedExcludedTags(
        tags: List<String>,
        excludedTags: List<String> = listOf()
    ): Map<String, Any> = mapOf<String, Any>(
        "includedTags[]" to tags,
        "excludedTags[]" to excludedTags,
        "includes[]" to "cover_art",
        "limit" to DEFAULT_COLLECTION_SIZE
    )

    fun onSessionLoaded(new: Session<Manga, MangaAttributes>) {
        cache.latestMangaSearch[sessionQueries]!!.from(new)
    }

    fun onPainterLoaded(i: Int, p: Painter) {
        val new = session.data[i].copy(painter = p)
        session.data[i] = new
        cache.latestMangaSearch[sessionQueries]!!.data[i] = new
    }

    fun onStatusUpdate(reading: Boolean, index: Int) {
        scope.launch {
            val res = if (reading) updateStatus(session.data[index], MangaStatus.Reading)
                else updateStatus(session.data[index], MangaStatus.None)
            res.also {
                if (session.state.value == SessionState.ACTIVE) session.data[index] = it
                cache.latestMangaSearch[sessionQueries]!!.data[index] = it
            }
        }
    }

    fun onOptionsClick() { showOptions = !showOptions  }

    fun navigateToCustomListScreen(nav: Navigator) {
        vm.navigateToCustomList(nav)
        showOptions = false
    }

    fun navigateToSettingsScreen(nav: Navigator) {
        vm.navigateToSettings(nav)
        showOptions = false
    }
}

