package viewmodel

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import util.ASCENDING

class ChapterScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache,
) : ScreenModel {
    val chapters = mutableStateListOf<Data<ChapterAttributes>>()
    val availableLanguages = mutableStateListOf<String>()
    var totalPages by mutableIntStateOf(1)
    var currentPage by mutableIntStateOf(1)
    var ableToNavigate by mutableStateOf(true)
    var showLoading by mutableStateOf(false)
    var showSettings by mutableStateOf(false)
    var language by mutableStateOf("")
    var order by mutableStateOf(ASCENDING)
    private val manga = SharedObject.detailManga
    private val mangaId = manga.data.id

    init {
        initLanguages()
        fetchChapters()
    }

    private fun getByLanguage(
        languages: List<String>,
        vararg prioritizedLanguage: String = arrayOf("en", "id")
    ): String {
        languages.forEach { language ->
            if (language in prioritizedLanguage) return language.also { this.language = language }
        }
        return languages[0].also { language = languages[0] }
    }

    private fun defaultQuery(language: String): String {
        val languageParam = generateArrayQueryParam(
            "translatedLanguage[]",
            listOf(language)
        )
        return generateQuery(
            queryParams = mapOf("order[chapter]" to order),
            otherParams = languageParam
        )
    }

    private fun initLanguages() {
        manga.data.attributes.availableTranslatedLanguages.let {
            it.forEach { s ->
                if (s != null) availableLanguages.add(s)
            }
        }
        getByLanguage(availableLanguages)
    }

    private fun fetchChapters() {
        val loadedChapters = cache.loadedChapters
        if (loadedChapters[mangaId] == null) {
            showLoading = true
            screenModelScope.launch {
                mangaDex.getMangaChapters(
                    mangaId = mangaId,
                    queries = defaultQuery(language)
                )?.let { response ->
                    chapters.addAll(response.data)
                    cache.loadedChapters[mangaId] = Chapters(response, response.data.toMutableList())
                    setTotalPages(response)
                    showLoading = false
                }
            }
        } else {
            chapters.addAll(loadedChapters[mangaId]!!.data)
            setTotalPages(loadedChapters[mangaId]!!.response)
            order = "asc"
        }
    }

    fun nextPage() {
        ableToNavigate = false
        val loadedChapters = cache.loadedChapters[mangaId]!!
        val limit = loadedChapters.response.limit
        val total = loadedChapters.response.total
        val nextPage = currentPage + 1
        var nextSize = limit * nextPage
        if (nextSize > total) nextSize = total
        if (loadedChapters.data.size < nextSize) {
            showLoading = true
            screenModelScope.launch {
                mangaDex.paging.chapters(loadedChapters.response, defaultQuery(language))?.let {
                    val chapters = it.data
                    cache.loadedChapters[mangaId]!!.response = it
                    cache.loadedChapters[mangaId]!!.data.addAll(chapters)
                    this@ChapterScreenModel.chapters.run {
                        clear()
                        addAll(chapters)
                    }
                    currentPage++
                    ableToNavigate = true
                    showLoading = false
                }
            }
        } else {
            val startIndex = currentPage * limit
            var endIndex = startIndex + limit - 1
            if (endIndex > total) endIndex = total - 1
            this@ChapterScreenModel.chapters.run {
                clear()
                addAll(loadedChapters.data.subList(startIndex, endIndex))
            }
            cache.loadedChapters[mangaId]!!.response = cache.loadedChapters[mangaId]!!.response.copy(
                offset = limit * currentPage
            )
            currentPage++
            ableToNavigate = true
        }
    }

    fun prevPage() {
        ableToNavigate = false
        val loadedChapters = cache.loadedChapters[mangaId]!!
        val limit = loadedChapters.response.limit
        val offset = loadedChapters.response.offset
        val endIndex = offset - 1
        val startIndex = offset - limit
        this@ChapterScreenModel.chapters.run {
            clear()
            addAll(loadedChapters.data.subList(startIndex, endIndex))
        }
        cache.loadedChapters[mangaId]!!.response = cache.loadedChapters[mangaId]!!.response.copy(
            offset = startIndex
        )
        currentPage--
        ableToNavigate = true
    }

    private fun setTotalPages(res: ListResponse<ChapterAttributes>) {
        val x: Float = res.total / res.limit.toFloat()
        totalPages = if (x < 1) 1 else {
            val int = x.toInt()
            if (x > int) int + 1 else int
        }
    }

    fun onSettingClick() { showSettings = !showSettings }

    fun onApplySettingsClick() {

    }
}