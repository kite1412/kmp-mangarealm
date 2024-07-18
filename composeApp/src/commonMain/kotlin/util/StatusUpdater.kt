package util

import Cache
import SharedObject
import api.mangadex.service.MangaDex
import model.Manga
import model.Status

interface StatusUpdater {
    val mangaDex: MangaDex
    val cache: Cache

    suspend fun updateStatus(
        manga: Manga,
        status: Status,
    ): Manga {
        val new = manga.copy(status = status)
        SharedObject.updatedManga = new
        mangaDex.updateMangaStatus(manga.data.id, status.rawStatus)
        cache.mangaStatus[manga.data.id] = new
        return new
    }
}