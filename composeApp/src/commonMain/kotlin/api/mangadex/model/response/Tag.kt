package api.mangadex.model.response

import api.mangadex.model.response.attribute.TagAttributes
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: String,
    val type: String,
    val attributes: TagAttributes,
    val relationships: List<Relationship>
)
