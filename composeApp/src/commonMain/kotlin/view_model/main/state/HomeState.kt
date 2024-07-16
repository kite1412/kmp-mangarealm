package view_model.main.state

import Cache
import Libs
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
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
import model.toMangaList
import util.ADVENTURE_TAG
import util.COMEDY_TAG
import util.DEFAULT_COLLECTION_SIZE
import util.KottageConst
import util.MYSTERY_TAG
import util.PSYCHOLOGICAL_TAG
import util.ROMANCE_TAG
import util.retry

class HomeState(
    private val scope: CoroutineScope,
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage,
    private val cache: Cache = Libs.cache
) {
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
        advComTags = listOf(tags[ADVENTURE_TAG]!!, tags[COMEDY_TAG]!!) to psyMysTags.first + listOf(tags[ROMANCE_TAG]!! )
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
                if (fromCache == null) {
                    sessionQueries = q
                    session.init(queries)
                    val res = mangaDex.getManga(q)
                    if (res != null) {
                        val data = res.toMangaList()
                        session.setActive(res, data)
                        cache.latestMangaSearch[q] = MangaSession().apply { from(session) }
                    }
                } else {
                    session.from(fromCache)
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
}

