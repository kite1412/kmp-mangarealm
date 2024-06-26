package model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(val tagId: String, val name: String)
