package api.mangadex.model.response

import api.mangadex.model.response.attribute.DynamicAttribute
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: DynamicAttribute? = null
)
