package api.mangadex.model.response

import api.mangadex.model.response.serializer.DescriptionSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DescriptionSerializer::class)
sealed class Description {
    @Serializable
    data class MapValue(val value: Map<String, String>) : Description()
    @Serializable
    data class StringValue(val value: String) : Description()
}