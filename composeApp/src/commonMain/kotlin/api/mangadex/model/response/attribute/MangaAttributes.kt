package api.mangadex.model.response.attribute

import api.mangadex.model.response.Tag
import kotlinx.serialization.Serializable

@Serializable
data class MangaAttributes(
    val title: Map<String, String> = mapOf(),
    val altTitles: List<Map<String, String>> = listOf(),
    val description: Map<String, String> = mapOf(),
    val isLocked: Boolean = false,
    val links: Map<String, String>? = null,
    val originalLanguage: String = "",
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val publicationDemographic: String? = null,
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String = "",
    val chapterNumbersResetOnNewVolume: Boolean = false,
    val availableTranslatedLanguages: List<String?> = listOf(),
    val latestUploadedChapter: String = "",
    val tags: List<Tag> = listOf(),
    val state: String = "",
    val version: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun EmptyMangaAttributes(): MangaAttributes {
    return MangaAttributes()
}