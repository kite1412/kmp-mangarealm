package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<ATTR>(
    val result: String = "",
    val response: String = "",
    val data: List<Data<ATTR>> = listOf(),
    val limit: Int = 0,
    val offset: Int = 0,
    val total: Int = 0,
    val errors: List<ErrorDetail>? = null
)
