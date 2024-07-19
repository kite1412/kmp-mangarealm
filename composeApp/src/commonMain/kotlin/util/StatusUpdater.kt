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

interface StatusUpdater {
    val mangaDex: MangaDex
    val sharedViewModel: SharedViewModel
    val cache: Cache

    fun updateStatus(
        manga: Manga,
        status: Status,
    ): Manga {
        val new = manga.copy(status = status)
        SharedObject.detailManga = new
        sharedViewModel.viewModelScope.launch {
            retry<UpdateStatusResponse?>(
                count = 3,
                predicate = { it == null || it.errors != null }
            ) {
                mangaDex.updateMangaStatus(manga.data.id, status.rawStatus).also {
                    if (status == MangaStatus.None) {
                        sharedViewModel.mangaStatus[manga.status]?.remove(manga)
                        sharedViewModel.mangaStatus[MangaStatus.All]!!.remove(manga)
                    } else {
                        sharedViewModel.mangaStatus[status]!!.add(new)
                        sharedViewModel.mangaStatus[manga.status]?.remove(manga)
                        sharedViewModel.updateMangaAllStatus(new)
                    }
                }
            }
        }
        return new
    }
}