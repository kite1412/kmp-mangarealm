package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse<T>(
    val result: String,
    val data: T? = null,
    val errors: List<ErrorDetail>? = null
)
