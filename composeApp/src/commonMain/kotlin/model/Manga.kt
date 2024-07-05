package model

import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.EmptyMangaAttributes
import api.mangadex.model.response.attribute.MangaAttributes
import kotlinx.serialization.Serializable

@Serializable
data class Manga(
    val data: Data<MangaAttributes>
)

fun emptyManga(): Manga {
    return Manga(Data(attributes = EmptyMangaAttributes()))
}
