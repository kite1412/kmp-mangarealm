package viewmodel.main.state

import Cache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Manga
import model.Session

class DiscoveryState(
    private val mangaDex: MangaDex,
    private val cache: Cache,
    private val scope: CoroutineScope
) {

    var searchBarValue by mutableStateOf("")
    var searchSession by mutableStateOf(Session())
    val searchData = mutableStateMapOf<String, Manga>()
    private var q: String = ""

    fun updateSearchData(queries: Map<String, Any> = mapOf()) {
        q = generateQuery(queries)
        scope.launch {
            searchSession.manga.clear()
            val fromCache = cache.latestMangaSearch[q]
            if (fromCache == null) {
                val res = mangaDex.getManga(q)
                if (res != null) {
                    searchData.putAll(
                        res.data.map {
                            Manga(it)
                        }.associateBy { it.data.id }.toMutableMap()
                    )
                    searchSession = searchSession.copy(
                        response = res,
                        manga = searchData
                    )
                    cache.latestMangaSearch[q] = searchSession
                }
            } else {
                searchSession = fromCache
                searchData.putAll(fromCache.manga)
            }
        }
    }

    fun searchBarValueChange(new: String) { searchBarValue = new }

    fun updateMangaPainter(manga: Manga, painter: Painter) {
        val id = manga.data.id
        val new = manga.copy(painter = painter)
        searchData[id] = new
        cache.latestMangaSearch[q]!!.manga[id] = new
    }
}