package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class HomeUrl(
    val result: String = "",
    val baseUrl: String = "",
    val chapter: ChapterImages = ChapterImages("", listOf(), listOf()),
    val errors: List<ErrorDetail>? = null
)