package viewmodel

import Cache
import Libs
import SharedObject
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Chapters
import util.ASCENDING

class ChapterScreenModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache,
) : ScreenModel, ReaderNavigator {
    val chapters = mutableStateListOf<Data<ChapterAttributes>>()
    val availableLanguages = mutableStateListOf<String>()
    var totalPages by mutableIntStateOf(1)
    var currentPage by mutableIntStateOf(1)
    var ableToNavigate by mutableStateOf(true)
    var showLoading by mutableStateOf(false)
    var showSettings by mutableStateOf(false)
    var languageSetting by mutableStateOf("")
    var orderSetting by mutableStateOf(ASCENDING)
    val applyEnabled by derivedStateOf {
        languageSetting != language || orderSetting != order
    }
    private var language by mutableStateOf("")
    private var order by mutableStateOf(ASCENDING)
    private val manga = SharedObject.detailManga
    private val mangaId = manga.data.id
    val chapterListState = LazyListState()

    init {
        initLanguages()
        fetchChapters()
    }

    private fun setLanguageOrder(
        languages: List<String>,
        vararg prioritizedLanguage: String = arrayOf("en", "id")
    ): String {
        val c = cache.chapters[mangaId]
        var l = ""
        if (c == null) {
            for (language in prioritizedLanguage) {
                if (language in languages) {
                    l = language
                    break
                }
            }
            if (l.isEmpty()) {
                l = languages[0]
            }
        } else {
            l =  c.language
            order = c.order
            orderSetting = c.order
        }
        return l.also {
            language = it
            languageSetting = it
        }
    }

    private fun queries(language: String, order: String): String {
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
        manga.data.attributes.availableTranslatedLanguages.forEach {
            if (it != null) availableLanguages.add(it)
        }
        setLanguageOrder(availableLanguages)
    }

    private fun fetchChapters() {
        val loadedChapters = cache.chapters[mangaId]
        if (loadedChapters == null) {
            showLoading = true
            screenModelScope.launch {
                mangaDex.getMangaChapters(
                    mangaId = mangaId,
                    queries = queries(language, order)
                )?.let { response ->
                    chapters.addAll(response.data)
                    cache.chapters[mangaId] = Chapters(
                        language = language,
                        order = order,
                        response = response,
                        data = response.data.toMutableList()
                    )
                    setTotalPages(response)
                    showLoading = false
                }
            }
        // exclusive for initialization
        } else {
            val limit = loadedChapters.response.limit
            val total = loadedChapters.response.total
            if (total < limit) chapters.addAll(loadedChapters())
                else chapters.addAll(loadedChapters().subList(0, limit - 1))
            setTotalPages(loadedChapters.response)
        }
    }

    fun nextPage() {
        ableToNavigate = false
        screenModelScope.launch {
            chapterListState.scrollToItem(0)
        }
        val loadedChapters = cache.chapters[mangaId]!!
        val limit = loadedChapters.response.limit
        val total = loadedChapters.response.total
        val nextPage = currentPage + 1
        var nextSize = limit * nextPage
        if (nextSize > total) nextSize = total
        if (loadedChapters().size < nextSize) {
            showLoading = true
            screenModelScope.launch {
                mangaDex.paging.chapters(loadedChapters.response, queries(language, order))?.let {
                    val chapters = it.data
                    cache.chapters[mangaId]!!.response = it
                    cache.chapters[mangaId]!!().addAll(chapters)
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
            cache.chapters[mangaId]!!.response = cache.chapters[mangaId]!!.response.copy(
                offset = limit * currentPage
            )
            currentPage++
            ableToNavigate = true
        }
    }

    fun prevPage() {
        ableToNavigate = false
        screenModelScope.launch {
            chapterListState.scrollToItem(0)
        }
        val loadedChapters = cache.chapters[mangaId]!!
        val limit = loadedChapters.response.limit
        val offset = loadedChapters.response.offset
        val endIndex = offset - 1
        val startIndex = offset - limit
        this@ChapterScreenModel.chapters.run {
            clear()
            addAll(loadedChapters.data.subList(startIndex, endIndex))
        }
        cache.chapters[mangaId]!!.response = cache.chapters[mangaId]!!.response.copy(
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

    fun onSettingClick() {
        if (!showSettings) {
            showSettings = true
        } else {
            showSettings = false
            screenModelScope.launch {
                delay(500)
                languageSetting = language
                orderSetting = order
            }
        }
    }

    fun onApplySettingsClick() {
        screenModelScope.launch {
            chapterListState.scrollToItem(0)
        }
        chapters.clear()
        cache.chapters[mangaId] = null
        showSettings = false
        language = languageSetting
        order = orderSetting
        currentPage = 1
        fetchChapters()
    }
}