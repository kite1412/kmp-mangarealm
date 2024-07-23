package view_model

import Cache
import Libs
import SharedObject
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ChapterKey
import util.ASCENDING

class ChapterScreenModel(
    private val sharedViewModel: SharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache,
) : ScreenModel, ReaderNavigator {
    val chapters = mutableStateListOf<Data<ChapterAttributes>>()
    val availableLanguages = mutableStateListOf<String>()
    var showSettings by mutableStateOf(false)
    var languageSetting by mutableStateOf("")
    var orderSetting by mutableStateOf(ASCENDING)
    val applyEnabled by derivedStateOf {
        languageSetting != language || orderSetting != order
    }
    private var language by mutableStateOf("")
    private var order by mutableStateOf(ASCENDING)
    private val manga = SharedObject.detailManga
    val chapterSession = derivedStateOf {
        sharedViewModel.chapterSessions[
            ChapterKey(
                mangaId = manga.data.id,
                queries = generateQuery(sharedViewModel.chapterDefaultQueries(language, order))
            )
        ]
    }
    private val mangaId = manga.data.id
    val chapterListState = LazyListState()
    var showWarning by mutableStateOf(false)
    var fetchingChaptersReadMarker by mutableStateOf(false)

    init {
        initLanguages()
        if (!showWarning) fetchChapters()
    }

    private fun setLanguageOrder(
        languages: List<String>,
        vararg prioritizedLanguage: String = arrayOf("en", "id")
    ): String {
        val c = cache.chapters[mangaId]
        var l = ""
        if (c == null) {
            if (languages.isEmpty()) {
                showWarning = true
                return ""
            }
            for (language in prioritizedLanguage) {
                if (language in languages) {
                    l = language
                    break
                }
            }
            if (l.isEmpty()) l = languages[0]
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

    private fun initLanguages() {
        manga.data.attributes.availableTranslatedLanguages.forEach {
            if (it != null) availableLanguages.add(it)
        }
        setLanguageOrder(availableLanguages)
    }

    private fun fetchChapters() {
        val queriesMap = sharedViewModel.chapterDefaultQueries(language, order)
        sharedViewModel.beginChapterSession(
            manga = manga,
            queries = queriesMap
        ) { s -> fetchingChaptersReadMarker = !s }
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
        fetchChapters()
    }
}