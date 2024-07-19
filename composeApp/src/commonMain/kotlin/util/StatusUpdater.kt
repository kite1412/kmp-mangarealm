package util

import Cache
import androidx.lifecycle.viewModelScope
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
        sharedViewModel.viewModelScope.launch {
            mangaDex.updateMangaStatus(manga.data.id, status.rawStatus).also {
                sharedViewModel.mangaStatus[new.status]?.add(new)
                if (status == MangaStatus.None) sharedViewModel.mangaStatus[manga.status]?.remove(manga)
            }
        }
        return new
    }
}