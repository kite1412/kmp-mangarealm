package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class IdType(
    val id: String,
    val type: String
)
