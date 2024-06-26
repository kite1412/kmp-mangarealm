package api.mangadex.model.response

import api.mangadex.model.response.attribute.DynamicAttributes
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: DynamicAttributes? = null
)
