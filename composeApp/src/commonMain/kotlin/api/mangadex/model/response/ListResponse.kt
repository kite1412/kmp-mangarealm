package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<ATTR, R>(
    val result: String,
    val response: String,
    val data: List<Data<ATTR>>,
    val relationships: List<Relationship<R>>
)
