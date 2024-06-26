package api.mangadex.model.response.attribute

import api.mangadex.model.response.Description
import api.mangadex.model.response.Tag
import kotlinx.serialization.Serializable

@Serializable
data class DynamicAttributes(
    // general
    val description: Description? = null,
    val version: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,

    // manga
    val title: Map<String, String>? = null,
    val altTitles: List<Map<String, String>>? = null,
    val isLocked: Boolean? = null,
    val links: Map<String, String>? = null,
    val originalLanguage: String? = null,
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val publicationDemographic: String? = null,
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String? = null,
    val chapterNumbersResetOnNewVolume: Boolean? = null,
    val availableTranslatedLanguages: List<String>? = null,
    val latestUploadedChapter: String? = null,
    val tags: List<Tag>? = null,
    val state: String? = null,

    // cover_art
    val volume: String? = null,
    val fileName: String? = null,
    val locale: String? = null,
)
