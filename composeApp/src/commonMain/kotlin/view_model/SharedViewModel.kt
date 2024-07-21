package view_model

import Libs
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.mangadex.service.MangaDex
import api.mangadex.util.generateQuery
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.Status
import model.getMangaIds
import model.session.CustomListSession
import model.session.isEmpty
import model.toCustomList
import util.retry

class SharedViewModel(
    private val mangaDex: MangaDex = Libs.mangaDex,
) : ViewModel() {
    val mangaStatus = mutableMapOf<Status, SnapshotStateList<Manga>>()
    val customListSession = CustomListSession()

    init {
        MangaStatus(true).forEach {
            mangaStatus[it] = mutableStateListOf()
        }
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

    fun beginSession() {
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
                                    it.mangaIds.addAll(it.data.getMangaIds())
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

    fun updateCustomListManga(index: Int, manga: List<Manga>) {
        viewModelScope.launch {
            customListSession.data[index].manga.addAll(manga)
        }
    }

    fun updateCustomListMangaPainter(customListIndex: Int, mangaIndex: Int, painter: Painter) {
        customListSession.data[customListIndex].manga[mangaIndex] =
            customListSession.data[customListIndex].manga[mangaIndex].copy(painter = painter)
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