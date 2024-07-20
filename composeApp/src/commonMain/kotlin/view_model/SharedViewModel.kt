package view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import model.Manga
import model.MangaStatus
import model.Status
import model.session.CustomListSession

class SharedViewModel : ViewModel() {
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

    fun updateCustomListSession(new: CustomListSession): CustomListSession =
        customListSession.clearFrom(new) as CustomListSession
}

fun SnapshotStateList<Manga>.removeManga(manga: Manga) {
    for ((i, m) in this.withIndex()) {
        if (m.data.id == manga.data.id) {
            this.removeAt(i)
            return
        }
    }
}