package view_model

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ChapterList
import model.Chapters
import model.MangaStatus
import util.ASCENDING
import util.StatusUpdater
import util.WARNING_TIME

class DetailScreenModel(
    override val sharedViewModel: SharedViewModel,
    override val mangaDex: MangaDex = Libs.mangaDex,
    override val cache: Cache = Libs.cache
) : ScreenModel, ReaderNavigator, ChapterNavigator, StatusUpdater {
    var titleTagsPadding by mutableStateOf(24)
    var isShowingDetail by mutableStateOf(false)
    var chapterListHeight by mutableStateOf(0)
    var readClicked by mutableStateOf(false)
    var showUpdateStatus by mutableStateOf(false)
    var status by mutableStateOf(MangaStatus.None)
    var manga by mutableStateOf(SharedObject.detailManga)
    var showWarning by mutableStateOf(false)
    var warning = ""
    var showPopNotice by mutableStateOf(false)

    init {
        status = manga.status
    }

    fun detailVisibility() {
        isShowingDetail = !isShowingDetail
    }

    fun onRead(nav: Navigator) {
        readClicked = true
        screenModelScope.launch {
            val chapters = cache.chapters[manga.data.id]
            if (chapters == null) {
                val availableLanguages = manga.data.attributes
                    .availableTranslatedLanguages
                val languages = availableLanguages.filterNotNull().filter {
                        it == "en" || it == "id"
                    }
                var l = ""
                if (languages.isNotEmpty()) {
                    for (lang in languages) {
                        if (lang == "en") {
                            l = lang
                            break
                        }
                        l = lang
                    }
                } else {
                    l =  if (availableLanguages.isNotEmpty()) manga.data.attributes
                        .availableTranslatedLanguages[0]!! else return@launch showWarning("No chapters found")
                }
                val lang = generateQuery(mapOf("translatedLanguage[]" to l))
                val res = mangaDex.getMangaChapters(
                    mangaId = manga.data.id,
                    queries = generateQuery(
                        queryParams = mapOf("order[chapter]" to ASCENDING),
                        otherParams = lang
                    )
                )
                if (res != null) {
                    if (res.errors != null) return@launch showWarning()
                    val chapterList = ChapterList(chapters = res.data)
                    cache.chapters[manga.data.id] = Chapters(
                        language = l,
                        order = ASCENDING,
                        response = res,
                        data = res.data.toMutableList()
                    )
                    if (res.data.isNotEmpty()) navigateToReader(
                        nav = nav,
                        chapterList = chapterList
                    )
                } else return@launch showWarning()
            } else navigateToReader(
                nav = nav,
                chapterList = ChapterList(chapters = chapters())
            )
            readClicked = false
        }
    }

    private suspend fun showWarning(message: String = "Please try again later") {
        warning = message
        showWarning = true
        delay(WARNING_TIME)
        showWarning = false
        readClicked = false
    }

    fun onAddToList() {

    }

    fun onStatus() {
        showUpdateStatus = true
        status = manga.status
    }

    fun onDeleteStatus() {
        screenModelScope.launch {
            showUpdateStatus = false
            status = MangaStatus.None
            manga = updateStatus(manga, MangaStatus.None)
        }
    }

    fun onUpdateStatus() {
        screenModelScope.launch {
            showUpdateStatus = false
            manga = updateStatus(manga, status)
        }
    }
}