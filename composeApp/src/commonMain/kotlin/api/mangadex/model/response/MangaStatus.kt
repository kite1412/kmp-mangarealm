package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MangaStatus(
    val result: String,
    val statuses: Map<String, String>
)
