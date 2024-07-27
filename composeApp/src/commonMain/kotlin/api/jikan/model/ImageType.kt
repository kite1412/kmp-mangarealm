package api.jikan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageType(
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("small_image_url")
    val smallImageUrl: String? = null,
    @SerialName("large_image_url")
    val largeImageUrl: String? = null
)
