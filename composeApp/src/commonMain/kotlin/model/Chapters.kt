package model

import api.mangadex.model.response.Data
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes

data class Chapters(
    var response: ListResponse<ChapterAttributes>,
    val data: MutableList<Data<ChapterAttributes>>
)