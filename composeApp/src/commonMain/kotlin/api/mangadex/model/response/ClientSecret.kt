package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ClientSecret(
    val result: String,
    val data: String
)
