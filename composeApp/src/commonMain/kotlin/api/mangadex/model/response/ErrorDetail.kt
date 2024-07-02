package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetail(
    val id: String,
    val status: Int,
    val title: String? = null,
    val detail: String? = null,
    val context: String? = null,
)
