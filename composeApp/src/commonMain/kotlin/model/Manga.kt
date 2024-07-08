package model

import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.EmptyMangaAttributes
import api.mangadex.model.response.attribute.MangaAttributes

data class Manga(
    val data: Data<MangaAttributes>,
    var status: Status? = null
)

fun emptyManga(): Manga {
    return Manga(Data(attributes = EmptyMangaAttributes()))
}
