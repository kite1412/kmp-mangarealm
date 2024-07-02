package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class EntityResponse<ATTR>(
    val result: String = "",
    val response: String = "",
    val data: Data<ATTR>? = null,
    val errors: List<ErrorDetail>? = null
)
