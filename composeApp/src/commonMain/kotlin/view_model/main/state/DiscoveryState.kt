package view_model.main.state

import Cache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.Status
import model.session.MangaSession
import model.session.Session
import model.toMangaList

class DiscoveryState(
    private val mangaDex: MangaDex,
    private val cache: Cache,
    private val scope: CoroutineScope
) {
    var searchBarValue by mutableStateOf("")
    val session = MangaSession()
    private var q: String = ""

    fun updateSession(queries: Map<String, Any> = mapOf()) {
        q = generateQuery(queries)
        scope.launch {
            session.clear()
            val fromCache = cache.latestMangaSearch[q]
            if (fromCache == null) {
                val res = mangaDex.getManga(q)
                if (res != null) {
                    val data = res.toMangaList()
                    session.newResponse(res)
                    session.putAllQueries(queries)
                    session.addAll(data)
                    cache.latestMangaSearch[q] = MangaSession().apply {
                        newResponse(res)
                        addAll(data)
                    }
                }
            } else session.from(fromCache)
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

    fun checkStatus(manga: Manga): Status? = if (manga.status != MangaStatus.None) manga.status
        else null
}