package viewmodel

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import api.mangadex.model.response.Data
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateArrayQueryParam
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import model.Chapters

class ChapterScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache,
) : ScreenModel {
    val chapters = mutableStateListOf<Data<ChapterAttributes>>()
    val availableLanguages = mutableStateListOf<String>()
    var totalPages by mutableIntStateOf(0)
    var currentPage by mutableIntStateOf(1)

    init {
        fetchChapters()
    }

    private fun getByLanguage(
        languages: List<String>,
        vararg prioritizedLanguage: String = arrayOf("en", "id")
    ): String {
        languages.forEach { language ->
            if (language in prioritizedLanguage) return language
        }
        return languages[0]
    }

    private fun fetchChapters() {
        val loadedChapters = cache.loadedChapters
        val manga = SharedObject.detailManga
        val mangaId = manga.data.id
        if (loadedChapters[mangaId] == null) {
            screenModelScope.launch {
                manga.data.attributes.availableTranslatedLanguages.let {
                    it.forEach { s ->
                        if (s != null) availableLanguages.add(s)
                    }
                }
                val language = generateArrayQueryParam(
                    "translatedLanguage[]",
                    listOf(getByLanguage(availableLanguages))
                )
                val response = mangaDex.getMangaChapters(
                    mangaId = mangaId,
                    queries = generateQuery(
                        queryParams = mapOf("order[chapter]" to "asc"),
                        otherParams = language
                    )
                )
                if (response != null) {
                    chapters.addAll(response.data)
                    cache.loadedChapters[mangaId] = Chapters(response, response.data)
                    setTotalPages(response)
                }
            }
        } else {
            chapters.addAll(loadedChapters[mangaId]!!.data)
            setTotalPages(loadedChapters[mangaId]!!.response)
        }
    }

    fun nextPage(prevRes: ListResponse<ChapterAttributes>) {

    }

    private fun setTotalPages(res: ListResponse<ChapterAttributes>) {
        val x: Float = res.total / res.limit.toFloat()
        totalPages = if (x < 1) 1 else {
            val int = x.toInt()
            if (x > int) int + 1 else int
        }
    }
}