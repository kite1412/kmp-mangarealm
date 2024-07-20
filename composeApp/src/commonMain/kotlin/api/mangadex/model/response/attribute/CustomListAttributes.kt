package api.mangadex.model.response.attribute

import kotlinx.serialization.Serializable

@Serializable
data class CustomListAttributes(
    val name: String,
    val visibility: String,
    val version: Int
)
