package api.jikan.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterAttributes(
    @SerialName("mal_id")
    val malId: Int,
    val url: String,
    val images: Images,
    val name: String
)
