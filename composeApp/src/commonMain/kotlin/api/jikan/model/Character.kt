package api.jikan.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val role: String,
    val character: CharacterAttributes
)
