package model

import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes

data class Session(
    val response: ListResponse<MangaAttributes>,
    val manga: MutableMap<String, Manga>
)
