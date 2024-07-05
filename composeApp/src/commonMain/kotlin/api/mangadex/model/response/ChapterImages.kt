package api.mangadex.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ChapterImages(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>,
)
