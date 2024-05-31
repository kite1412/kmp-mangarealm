package api.mangadex.model.response.serializer

import api.mangadex.model.response.Description
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object DescriptionSerializer : KSerializer<Description> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Description", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Description {
        return when (val input: JsonElement? = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonObject -> Description.MapValue(input.map { it.key to it.value.jsonPrimitive.content }.toMap())
            else -> Description.StringValue(input?.jsonPrimitive?.content ?: "")
        }
    }

    override fun serialize(encoder: Encoder, value: Description) {
        TODO("Not yet implemented")
    }
}