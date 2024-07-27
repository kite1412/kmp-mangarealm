package api.jikan.model

import kotlinx.serialization.Serializable

@Serializable
data class Data<T>(
    val data: T
)
