package io.github.mystere.serialization.cqcode.raw

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.logger
import io.github.mystere.serialization.cqcode.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


@Serializable(with = CQCodeRawMessageItemSerializer::class)
data class CQCodeRawMessageItem(
    @SerialName("type")
    override val _type: String,
    @SerialName("data")
    val data: Map<String, String>,
): ICQCodeMessageItem, ICQCodeMessageItemOperator<CQCodeRawMessageItem, CQCodeRawMessage> {
    override fun ArrayDeque<CQCodeRawMessageItem>.asMessage(): CQCodeRawMessage {
        return CQCodeRawMessage(this)
    }
}

object CQCodeRawMessageItemSerializer: KSerializer<CQCodeRawMessageItem> {
    private val log by logger()

    override val descriptor: SerialDescriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): CQCodeRawMessageItem {
        when (decoder) {
            is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
                is JsonObject -> {
                    return CQCodeRawMessageItem(
                        element["type"]!!.jsonPrimitive.content,
                        MystereJson.decodeFromJsonElement(element["data"]!!.jsonObject),
                    )
                }
                is JsonPrimitive -> return deserialize(CQCodeRawMessageItemDecoder(
                    element.content, decoder.serializersModule
                ))
                else -> { }
            }
            is CQCodeRawMessageItemDecoder -> return CQCodeRawMessageItem(
                decoder.type, decoder.args
            )
        }
        throw SerializationException("Unsupported decoder type: ${decoder::class}")
    }

    override fun serialize(encoder: Encoder, value: CQCodeRawMessageItem) {
        when (encoder) {
            is JsonEncoder -> {
                encoder.encodeJsonElement(buildJsonObject {
                    put("type", value._type)
                    put("data", MystereJson.encodeToJsonElement(value.data))
                })
            }
            is CQCodeRawMessageItemEncoder -> { }
            else -> throw SerializationException("Unsupported decoder type: ${encoder::class}")
        }
    }
}
