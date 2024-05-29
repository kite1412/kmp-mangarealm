package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Relationship<T>(
    val id: String,
    val type: String,
    val related: String?,
    val attributes: T?
)
