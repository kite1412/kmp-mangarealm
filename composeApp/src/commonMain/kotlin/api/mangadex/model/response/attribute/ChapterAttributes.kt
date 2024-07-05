package api.mangadex.model.response.attribute

import api.mangadex.model.response.Data
import kotlinx.serialization.Serializable

@Serializable
data class ChapterAttributes(
    val title: String? = null,
    val volume: String? = null,
    val chapter: String? = null,
    val pages: Int,
    val translatedLanguage: String,
    val uploader: String? = null,
    val externalUrl: String? = null,
    val version: Int,
    val createdAt: String,
    val updatedAt: String,
    val publishAt: String,
    val readableAt: String,
)

fun emptyChapterAttributes(): ChapterAttributes = ChapterAttributes(
    pages = 0,
    translatedLanguage = "",
    uploader = "",
    version = 0,
    createdAt = "",
    updatedAt = "",
    publishAt = "",
    readableAt =""
)

fun emptyChapter(): Data<ChapterAttributes> = Data(attributes = emptyChapterAttributes())