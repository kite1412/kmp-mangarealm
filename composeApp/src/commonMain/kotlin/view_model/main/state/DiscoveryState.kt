package view_model.main.state

import Cache
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import io.github.irgaly.kottage.KottageList
import io.github.irgaly.kottage.KottageListPage
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.add
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.Status
import model.session.MangaSession
import model.session.Session
import model.session.SessionState
import model.toMangaList
import util.KottageConst

class DiscoveryState(
    private val mangaDex: MangaDex,
    private val cache: Cache,
    private val scope: CoroutineScope,
    private val kottageStorage: KottageStorage
) {
    var searchBarValue by mutableStateOf("")
    val session = MangaSession()
    val histories = mutableStateListOf<String>()
    private var q: String = ""
    private var initialized = false
    val listState = LazyListState()
    private val historyList = kottageStorage.list(KottageConst.HISTORY_LIST)

    fun init() {
        if (!initialized) {
            initHistory()
            initialized = true
        }
    }

    fun updateSession(queries: Map<String, Any> = mapOf()) {
        scope.launch {
            queries["title"]?.let {
                saveHistory(it as String)
            }
            q = generateQuery(queries)
            session.clear()
            val fromCache = cache.latestMangaSearch[q]
            if (fromCache == null) {
                session.state.value = SessionState.FETCHING
                val res = mangaDex.getManga(q)
                if (res != null) {
                    val data = res.toMangaList()
                    session.newResponse(res)
                    session.putAllQueries(queries)
                    session.addAll(data)
                    session.state.value = SessionState.ACTIVE
                    cache.latestMangaSearch[q] = MangaSession().apply { from(session) }
                }
            } else session.from(fromCache)
            listState.scrollToItem(0)
        }
    }

    fun searchBarValueChange(new: String) { searchBarValue = new }

    fun updateMangaPainter(index: Int, manga: Manga, painter: Painter) {
        val new = manga.copy(painter = painter)
        session.data[index] = new
        cache.latestMangaSearch[q]!!.data[index] = new
    }

    fun onSessionLoaded(newSession: Session<Manga, MangaAttributes>) {
        cache.latestMangaSearch[q]!!.from(newSession)
    }

    fun clearSession() {
        session.clear()
        searchBarValue = ""
    }

    private fun initHistory() {
        scope.launch {
            val pageSize = 100L
            val page0 = historyList.getPageFrom(null, pageSize)
            loadHistories(histories = historyList, initialPage = page0, pageSize = pageSize)
        }
    }

    private suspend fun loadHistories(histories: KottageList, initialPage: KottageListPage, pageSize: Long) {
        this.histories.addAll(initialPage.items.map { it.value<String>() })
        if (initialPage.hasNext) loadHistories(
            histories = histories,
            initialPage = histories.getPageFrom(initialPage.nextPositionId, pageSize),
            pageSize = pageSize
        )
    }

    private suspend fun saveHistory(history: String) {
        historyList.add(history, history)
        histories.add(history)
    }

    fun checkStatus(manga: Manga): Status? = if (manga.status != MangaStatus.None) manga.status
        else null
}