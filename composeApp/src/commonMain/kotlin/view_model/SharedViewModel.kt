package view_model

import Libs
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import io.github.irgaly.kottage.KottageStorage
import io.github.irgaly.kottage.getOrNull
import kotlinx.coroutines.launch
import model.AppSettings
import model.ChapterKey
import model.CustomList
import model.Manga
import model.MangaStatus
import model.Status
import model.getMangaIds
import model.session.ChapterSession
import model.session.CustomListSession
import model.session.isEmpty
import model.toChapters
import model.toCustomList
import util.KottageConst
import util.Log
import util.retry

class SharedViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
    private val kottageStorage: KottageStorage = Libs.kottageStorage
) : ViewModel() {
    val mangaStatus = mutableMapOf<Status, SnapshotStateList<Manga>>()
    val customListSession = CustomListSession()
    val chapterSessions = mutableStateMapOf<ChapterKey, ChapterSession>()
    var currentChapterSessionKey = ChapterKey()
    val appSettings = AppSettings()
    val chapterReadMarkers = mutableStateMapOf<String, SnapshotStateList<String>>()

    init {
        viewModelScope.launch {
            MangaStatus(true).forEach {
                mangaStatus[it] = mutableStateListOf()
            }
            loadAppSettings()
        }
    }

    private suspend fun loadAppSettings() {
        appSettings.isDarkMode.value = kottageStorage.getOrNull<Boolean>(KottageConst.THEME_MODE) ?: false
        appSettings.enableSwipeToPop.value = kottageStorage.getOrNull<Boolean>(KottageConst.SWIPE_TO_POP) ?: true
        appSettings.initialized.value = true
    }

    fun onMangaStatusPainterLoaded(status: Status, painter: Painter, index: Int) {
        mangaStatus[status]?.let {
            it.set(index, it[index].copy(painter = painter))
        }
    }

    fun findMangaStatus(manga: Manga): Manga? =
        mangaStatus[MangaStatus.All]!!.find { it.data.id == manga.data.id }

    fun updateMangaAllStatus(manga: Manga) {
        for ((i, m) in mangaStatus[MangaStatus.All]!!.withIndex()) {
            if (m.data.id == manga.data.id) {
                mangaStatus[MangaStatus.All]!![i] = manga
                return
            }
        }
        mangaStatus[MangaStatus.All]!!.add(manga)
    }

    fun beginCustomListSession() {
        viewModelScope.launch {
            if (customListSession.isEmpty()) {
                customListSession.init(customListSession.queries)
                val res = retry(
                    count = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    mangaDex.getUserCustomLists(generateQuery(customListSession.queries))
                }
                if (res != null) {
                    val new = CustomListSession().apply {
                        setActive(
                            response = res,
                            data = res.data.map { it.toCustomList() }
                                .map {
                                    it.mangaIds.addAll(it.data.value.getMangaIds())
                                    it
                                }
                        )
                    }
                    updateCustomListSession(new)
                }
            }
        }
    }

    private fun updateCustomListSession(new: CustomListSession): CustomListSession =
        customListSession.clearFrom(new) as CustomListSession

    fun deleteDeletedCustomList() = customListSession.data.removeAll { it.deleted }

    fun updateCustomListManga(customList: CustomList, manga: List<Manga>) = viewModelScope.launch {
        customList.manga.clear()
        customList.manga.addAll(manga)
    }

    fun updateCustomListMangaPainter(customList: CustomList, mangaIndex: Int, painter: Painter) {
        customList.manga[mangaIndex] =
            customList.manga[mangaIndex].copy(painter = painter)
    }

    fun addCustomListManga(customList: CustomList, manga: Manga) {
        viewModelScope.launch {
            customList.manga.add(manga)
            customList.mangaIds.add(manga.data.id)
            retry(
                count = 3,
                predicate = { false }
            ) {
                mangaDex.addMangaToCustomList(manga.data.id, customList.data.value.id)
            }.also {
                if (!it) customList.manga.removeManga(manga)
                    else customList.data.value.attributes.version++
            }
        }
    }

    fun removeCustomListManga(customList: CustomList, manga: Manga) {
        viewModelScope.launch {
            customList.manga.removeManga(manga)
            customList.mangaIds.removeAll { it == manga.data.id }
            retry(
                count = 3,
                predicate = { false }
            ) {
                mangaDex.removeMangaFromCustomList(manga.data.id, customList.data.value.id)
            }.also {
                if (!it) customList.manga.add(manga)
                    else customList.data.value.attributes.version++
            }
        }
    }

    suspend fun editCustomList(customList: CustomList, data: CustomListAttributes) {
        retry<EntityResponse<CustomListAttributes>?>(
            count = 3,
            predicate = { it == null || it.errors != null }
        ) {
            mangaDex.editCustomList(customList.data.value.id, data)?.also {
                it.errors?.joinToString()?.let { m -> Log.w(m) }
            }
        }?.also {
            if (it.errors == null) customList.data.value = it.data!!
        }
    }

    fun chapterDefaultQueries(language: String, order: String): Map<String, Any> =
        mapOf(
            "translatedLanguage[]" to language,
            "order[chapter]" to order,
            "limit" to 100,
            "offset" to 0
        )

    private fun initChapterReadMarker(
        mangaId: String,
        session: ChapterSession,
        state: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            state(false)
            retry(
                count = 3,
                predicate = { r -> r == null || r.errors != null }
            ) {
                mangaDex.getMangaReadMarkers(mangaId)
            }?.let { r ->
                r.data?.let {
                    val key = mangaId + session.queries["translatedLanguage[]"]
                    if (chapterReadMarkers[key] == null) {
                        chapterReadMarkers[key] = mutableStateListOf<String>().apply {
                            addAll(it)
                        }
                    }
                }
            }
            state(true)
        }
    }

    fun beginChapterSession(
        manga: Manga,
        queries: Map<String, Any>,
        readMarkerFetchState: (Boolean) -> Unit = {},
        onFailure: () -> Unit = {},
        onSuccess: (ChapterSession) -> Unit = {}
    ) {
        viewModelScope.launch {
            val q = generateQuery(queries)
            currentChapterSessionKey = ChapterKey(manga.data.id, q)
            if (chapterSessions[currentChapterSessionKey] == null) {
                chapterSessions[currentChapterSessionKey] = ChapterSession(manga.data.id)
                val s = chapterSessions[currentChapterSessionKey]!!
                s.init(queries)
                val res = retry<ListResponse<ChapterAttributes>?>(
                    count = 3,
                    predicate = { it == null || it.errors != null }
                ) {
                    mangaDex.getMangaChapters(manga.data.id, q)
                }
                if (res != null) {
                    s.setActive(res, res.toChapters())
                    initChapterReadMarker(manga.data.id, s, readMarkerFetchState)
                } else return@launch onFailure()
            }
            return@launch onSuccess(chapterSessions[currentChapterSessionKey]!!)
        }
    }
}

fun SnapshotStateList<Manga>.removeManga(manga: Manga) {
    for ((i, m) in this.withIndex()) {
        if (m.data.id == manga.data.id) {
            this.removeAt(i)
            return
        }
    }
}

fun SnapshotStateList<Manga>.containsManga(manga: Manga): Boolean = any { it.data.id == manga.data.id }