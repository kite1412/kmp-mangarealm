package api.mangadex.model.response.attribute

import api.mangadex.model.response.Description
import api.mangadex.model.response.Tag
import kotlinx.serialization.Serializable

@Serializable
data class DynamicAttributes(
    // general
    val version: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,

    // manga
    val title: Map<String, String>? = null,
    val altTitles: List<Map<String, String>>? = null,
    val description: Description? = null,
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

    // author
    val name: String? = null,
    val imageUrl: String? = null,
    val biography: Map<String, String> = mapOf(),
    val twitter: String? = null,
    val pixiv: String? = null,
    val melonBook: String? = null,
    val fanBox: String? = null,
    val booth: String? = null,
    val namicomi: String? = null,
    val nicoVideo: String? = null,
    val skeb: String? = null,
    val fantia: String? = null,
    val tumblr: String? = null,
    val youtube: String? = null,
    val weibo: String? = null,
    val naver: String? = null,
    val website: String? = null,
)
