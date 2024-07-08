package model

import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes

data class ChapterList(
    val index: Int = 0,
    val chapters: List<Data<ChapterAttributes>> = listOf()
) {
    operator fun invoke(): List<Data<ChapterAttributes>> = chapters
}
