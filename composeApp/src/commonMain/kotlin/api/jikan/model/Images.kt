package api.jikan.model

import kotlinx.serialization.Serializable

@Serializable
data class Images(
    val jpg: ImageType,
    val webp: ImageType
)
