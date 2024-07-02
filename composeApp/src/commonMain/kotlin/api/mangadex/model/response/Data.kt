package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Data<T>(
    val id: String = "",
    val type: String = "",
    val attributes: T,
    val relationships: List<Relationship> = listOf()
)