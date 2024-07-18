package view_model.main.state

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.Status
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
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
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG
import util.StatusUpdater
import util.retry
import view_model.main.MainViewModel

class HomeState(
    private val vm: MainViewModel,
    private val scope: CoroutineScope = vm.viewModelScope,
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
    val continueReading = mutableStateListOf<Manga>()

    private var _username = mutableStateOf("")
    val username = _username
    private var tags = mapOf<String, String>()
    var romComTags = listOf<String>() to listOf<String>()
    var advComTags = listOf<String>() to listOf<String>()
    var psyMysTags = listOf<String>() to listOf<String>()
    val session = MangaSession()
    private var sessionQueries = ""

    suspend fun updateUsername() {
        val username = kottageStorage.get<String>(KottageConst.USERNAME)
        _username.value = username
    }

    fun setTags(tags: Map<String, String>) {
        this.tags = tags
        psyMysTags = listOf(tags[PSYCHOLOGICAL_TAG]!!, tags[MYSTERY_TAG]!!) to listOf(tags[COMEDY_TAG]!!)
        romComTags = listOf(tags[ROMANCE_TAG]!!, tags[COMEDY_TAG]!!) to psyMysTags.first
        advComTags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!) to (psyMysTags.first + listOf(tags[ROMANCE_TAG]!!))
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

    fun initContinueReadingData() {
        scope.launch {
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
                            if (cache.mangaStatus.containsKey(it.data.id)) it.copy(
                                status = MangaStatus.Reading
                            ) else it
                        }
                        session.setActive(res, data)
                        cache.latestMangaSearch[q] = MangaSession().apply { from(session) }
                    }
                } else {
                    session.from(fromCache).also {
                        it.data.forEachIndexed { i, m ->
                            if (cache.mangaStatus[m.data.id]?.status == MangaStatus.Reading)
                                it.data[i] = m.copy(status = MangaStatus.Reading)
                            else if (m.status == MangaStatus.Reading && !cache.mangaStatus.containsKey(m.data.id))
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
            syncReadingStatus()
        }
    }

    fun syncReadingStatus() {
        val reading = cache.mangaStatus.filter { it.value.status == MangaStatus.Reading }
        if (reading.size != continueReading.size) {
            if (continueReading.size < reading.size) continueReading.add(SharedObject.updatedManga)
            else {
                continueReading.removeAll { SharedObject.updatedManga.data.id == it.data.id }
                cache.mangaStatus.remove(SharedObject.updatedManga.data.id)
            }
        }
    }
}

