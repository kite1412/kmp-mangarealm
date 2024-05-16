package api.mangadex.model.response.attribute

import kotlinx.serialization.Serializable

@Serializable
data class ClientAttributes(
    val name: String,
    val description: String,
    val profile: String,
    val externalClientId: String,
    val isActive: Boolean,
    val state: String,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
)
