package api.mangadex.model.response.attribute

import api.mangadex.model.response.Tag
import kotlinx.serialization.Serializable

@Serializable
data class MangaAttributes(
    val title: Map<String, String>,
    val altTitles: List<Map<String, String>>,
    val description: Map<String, String>,
    val isLocked: Boolean,
    val links: Map<String, String>? = null,
    val originalLanguage: String,
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val publicationDemographic: String? = null,
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String,
    val chapterNumbersResetOnNewVolume: Boolean,
    val availableTranslatedLanguages: List<String?>,
    val latestUploadedChapter: String,
    val tags: List<Tag>,
    val state: String,
    val version: Int,
    val createdAt: String,
    val updatedAt: String
)