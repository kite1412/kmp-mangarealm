package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse(
    val result: String,
    val errors: List<ErrorDetail>? = null
)
