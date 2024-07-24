package view_model

import Cache
import Libs
import SharedObject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import api.mangadex.model.request.CreateCustomList
import api.mangadex.model.request.Visibility
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ChapterKey
import model.ChapterList
import model.Chapters
import model.CustomList
import model.MangaStatus
import model.Status
import model.session.isEmpty
import model.toCustomList
import util.ASCENDING
import util.StatusUpdater
import util.WARNING_TIME
import util.retry

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
    var updating by mutableStateOf(false)
    var showWarning by mutableStateOf(false)
    var warning = ""
    var showPopNotice by mutableStateOf(false)
    var showAddToList by mutableStateOf(false)
    var showAddListPrompt by mutableStateOf(false)
    var textFieldValue by mutableStateOf("")
    var visibility by mutableStateOf(Visibility.PRIVATE)

    init {
        status = manga.status
    }

    fun detailVisibility() {
        isShowingDetail = !isShowingDetail
    }

    fun onRead(nav: Navigator) {
        readClicked = true
        screenModelScope.launch {
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
            val q = sharedViewModel.chapterDefaultQueries(l, ASCENDING)
            val key = ChapterKey(manga.data.id, generateQuery(q))
            sharedViewModel.currentChapterSessionKey = key
            sharedViewModel.beginChapterSession(
                manga = manga,
                queries = q,
                onFailure = {
                    screenModelScope.launch { showWarning() }
                }
            ) {
                screenModelScope.launch {
                    cache.chapters[manga.data.id] = Chapters(
                        language = l,
                        order = ASCENDING,
                        response = it.response,
                        data = it.response.data.toMutableList()
                    )
                    if (it.data.isNotEmpty()) navigateToReader(nav, ChapterList(0, it.data))
                        else showWarning()
                }
            }
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
        showAddToList = true
        if (sharedViewModel.customListSession.isEmpty()) sharedViewModel.beginCustomListSession()
    }

    fun onStatus() {
        showUpdateStatus = true
        status = manga.status
    }

    fun onDeleteStatus() {
        status = MangaStatus.None
        update(MangaStatus.None)
    }

    fun onUpdateStatus() = update(status)

    private fun update(status: Status) {
        showUpdateStatus = false
        manga = updateStatus(manga, status) { done, error ->
            updating = !done
            if (error) screenModelScope.launch {
                showWarning("Failed to update status")
            }
        }
    }

    fun onAddToListDismiss() {
        showAddToList = false
    }

    fun onCustomListClick(customList: CustomList) {
        if (customList.mangaIds.contains(manga.data.id)) sharedViewModel.removeCustomListManga(customList, manga)
            else sharedViewModel.addCustomListManga(customList, manga)
    }

    fun onAddNewList() {
        screenModelScope.launch {
            showAddListPrompt = false
            retry<EntityResponse<CustomListAttributes>?>(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.createCustomList(
                    CreateCustomList(
                        name = textFieldValue,
                        visibility = visibility
                    )
                )
            }?.let {
                sharedViewModel.customListSession.data.add(it.data!!.toCustomList())
                showWarning("List created")
            }
        }
    }

    fun onAddListPromptDismiss() {
        showAddListPrompt = false
        textFieldValue = ""
        visibility = Visibility.PRIVATE
    }
}