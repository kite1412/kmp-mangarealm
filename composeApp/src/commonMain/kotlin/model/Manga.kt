package model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.painter.Painter
import api.jikan.model.Character
import api.mangadex.model.response.Data
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.EmptyMangaAttributes
import api.mangadex.model.response.attribute.MangaAttributes

data class Manga(
    val data: Data<MangaAttributes>,
    var status: Status = MangaStatus.None,
    var painter: Painter? = null,
    val characters: SnapshotStateList<Character> = mutableStateListOf()
)

fun emptyManga(): Manga {
    return Manga(Data(attributes = EmptyMangaAttributes()))
}

fun ListResponse<MangaAttributes>.toMangaMap(): Map<String, Manga> {
    return data.map {
        Manga(it)
    }.associateBy { it.data.id }
}

fun ListResponse<MangaAttributes>.toMangaList(): List<Manga> = data.map(::Manga)