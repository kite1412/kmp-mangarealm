package api.mangadex.model.response.attribute

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