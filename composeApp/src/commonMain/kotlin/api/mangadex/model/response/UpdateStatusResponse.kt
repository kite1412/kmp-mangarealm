package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStatusResponse(
    val result: String,
    val errors: List<ErrorDetail>? = null
)
