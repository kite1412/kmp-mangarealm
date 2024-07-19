package util

import Cache
import SharedObject
import androidx.lifecycle.viewModelScope
import api.mangadex.model.response.UpdateStatusResponse
import api.mangadex.service.MangaDex
import kotlinx.coroutines.launch
import model.Manga
import model.MangaStatus
import model.Status
import view_model.SharedViewModel
import view_model.removeManga

interface StatusUpdater {
    val mangaDex: MangaDex
    val sharedViewModel: SharedViewModel
    val cache: Cache

    fun updateStatus(
        manga: Manga,
        status: Status,
        done: (done: Boolean, error: Boolean) -> Unit = { _, _ -> },
    ): Manga {
        val new = manga.copy(status = status)
        SharedObject.detailManga = new
        done(false, false)
        sharedViewModel.viewModelScope.launch {
            val res = retry<UpdateStatusResponse?>(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.updateMangaStatus(manga.data.id, status.rawStatus)
            }
            if (res == null) {
                done(true, true)
            } else  {
                if (status == MangaStatus.None) {
                    sharedViewModel.mangaStatus[manga.status]?.removeManga(manga)
                    sharedViewModel.mangaStatus[MangaStatus.All]!!.removeManga(manga)
                } else {
                    sharedViewModel.mangaStatus[status]!!.add(new)
                    sharedViewModel.mangaStatus[manga.status]?.removeManga(manga)
                    sharedViewModel.updateMangaAllStatus(new)
                }
                done(true, false)
            }
        }
        return new
    }
}