package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<ATTR>(
    val result: String,
    val response: String,
    val data: List<Data<ATTR>>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)
