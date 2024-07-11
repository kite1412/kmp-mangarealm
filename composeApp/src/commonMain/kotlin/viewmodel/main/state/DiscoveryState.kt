package viewmodel.main.state

import Cache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Manga
import model.MangaSession

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
                    val data = res.data.map {
                        Manga(it)
                    }.associateBy { it.data.id }.toMutableMap()
                    session.addQueries(q)
                    session.newResponse(res)
                    session.putAll(data)
                    cache.latestMangaSearch[q] = MangaSession().apply {
                        addQueries(q)
                        newResponse(res)
                        putAll(data.toMap())
                    }
                }
            } else session.from(fromCache)
        }
    }

    fun searchBarValueChange(new: String) { searchBarValue = new }

    fun updateMangaPainter(manga: Manga, painter: Painter) {
        val id = manga.data.id
        val new = manga.copy(painter = painter)
        session.data[id] = new
        cache.latestMangaSearch[q]!!.data[id] = new
    }
}