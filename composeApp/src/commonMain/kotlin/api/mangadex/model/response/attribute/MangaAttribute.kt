package api.mangadex.model.response.attribute

import kotlinx.serialization.Serializable

@Serializable
data class MangaAttribute(
    val title: Map<String, String>,
    val altTitles: List<Map<String, String>>,
    val description: Map<String, String>
)