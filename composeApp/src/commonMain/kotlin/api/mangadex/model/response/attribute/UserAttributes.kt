package api.mangadex.model.response.attribute

import kotlinx.serialization.Serializable

@Serializable
data class UserAttributes(
    val username: String,
    val roles: List<String>,
    val version: Int
)
