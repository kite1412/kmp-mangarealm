package view_model.main.state

import Cache
import Libs
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.viewModelScope
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
import model.toMangaList
import util.DEFAULT_COLLECTION_SIZE
import util.KottageConst
import util.retry
import view_model.SharedViewModel
import view_model.main.MainViewModel

class DiscoveryState(
    private val vm: MainViewModel,
    private val sharedViewModel: SharedViewModel = vm.sharedViewModel,
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val cache: Cache = Libs.cache,
    private val scope: CoroutineScope = vm.viewModelScope,
    private val kottageStorage: KottageStorage = Libs.kottageStorage
) {
    private val maxHistory = 50

    var searchBarValue by mutableStateOf("")
    val session = MangaSession()
    val listState = LazyListState()
    var histories = mutableStateListOf<String>()
    var showAllHistories by mutableStateOf(false)
    var showHistoryOptions by mutableStateOf(false)
    var historyEditing by mutableStateOf(false)
    val selectedHistory = mutableStateListOf<String>()
    var showDeletionWarning by mutableStateOf(false)
    var deletionMessage = ""
    var deletionAction = {}
    var deletionLabel = ""
    private var q: String = ""
    private var initialized = false
    private val historyList = kottageStorage.list(KottageConst.HISTORY_LIST)

    fun init() {
        if (!initialized) {
            initHistory()
            initialized = true
        }
    }

    private fun updateSession(queries: Map<String, Any> = mapOf()) {
        scope.launch {
            q = generateQuery(queries)
            session.clear()
            val fromCache = cache.latestMangaSearch[q]
            if (fromCache == null) {
                session.init(queries)
                val res = retry(
                    count = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    mangaDex.getManga(q)
                }
                if (res != null) {
                    val data = res.toMangaList().map {
                        sharedViewModel.findMangaStatus(it) ?: it
                    }
                    session.setActive(res, data)
                    cache.latestMangaSearch[q] = MangaSession().apply { from(session) }
                }
            } else session.from(fromCache).apply {
                data.mapIndexed { i, m ->
                    data[i] = sharedViewModel.findMangaStatus(m) ?: m
                }
            }
            listState.scrollToItem(0)
            queries["title"]?.let {
                saveHistory(it as String)
            }
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
            val pageSize = maxHistory.toLong()
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
        val temp = mutableListOf<String>().apply {
            add(history)
            val temp = mutableListOf<String>().apply {
                addAll(histories)
                remove(history)
            }
            addAll(temp)
            if (size > maxHistory) removeLast()
        }
        historyList.removeAll(true)
        histories.clear()
        for (i in 0 until temp.size) {
            historyList.add(temp[i], temp[i])
            histories.add(temp[i])
        }
    }

    fun deleteHistory(history: String) {
        scope.launch {
            kottageStorage.remove(history)
            histories.remove(history)
        }
    }

    fun beginSession(
        keyboardController: SoftwareKeyboardController?,
        focusManager: FocusManager,
        search: String = ""
    ) {
        if (search.isNotEmpty()) searchBarValue = search
        if (searchBarValue.isNotEmpty()) {
            keyboardController?.hide()
            focusManager.clearFocus()
            updateSession(
                queries = mapOf(
                    "title" to searchBarValue,
                    "includes[]" to "cover_art",
                    "limit" to DEFAULT_COLLECTION_SIZE
                )
            )
        }
    }

    fun onEditClick() {
        showHistoryOptions = false
        historyEditing = true
    }

    fun handleHistoryClick(
        keyboardController: SoftwareKeyboardController?,
        focusManager: FocusManager,
        history: String = ""
    ) {
        if (!historyEditing) beginSession(
            keyboardController = keyboardController,
            focusManager = focusManager,
            search = history
        ) else {
            if (!selectedHistory.contains(history)) selectedHistory.add(history)
                else selectedHistory.remove(history)
        }
    }

    fun clearSelectedHistory() {
        selectedHistory.clear()
        historyEditing = false
    }

    fun showDeletionWarning(message: String, deletionLabel: String = "Delete", action: () -> Unit) {
        deletionMessage = message
        deletionAction = action
        this.deletionLabel = deletionLabel
        showDeletionWarning = true
    }

    fun deleteSelectedHistories() {
        scope.launch {
            selectedHistory.apply {
                histories.removeAll(this)
                forEach {
                    kottageStorage.remove(it)
                }
                clear()
            }
            cancelDeletion()
            clearSelectedHistory()
        }
    }

    fun cancelDeletion() { showDeletionWarning = false }

    fun clearHistories() {
        scope.launch {
            historyList.removeAll(true)
            histories.clear()
            cancelDeletion()
            showHistoryOptions = false
        }
    }

    fun checkStatus(manga: Manga): Status? = if (manga.status != MangaStatus.None) manga.status
        else null
}