package io.github.mystere.serialization.cqcode

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = CQCodeMessageSerializer::class)
data class CQCodeMessage(
    internal val chain: ArrayDeque<CQCodeMessageItem> = ArrayDeque()
): List<CQCodeMessageItem> by chain {
    operator fun plus(next: CQCodeMessage): CQCodeMessage {
        return CQCodeMessage(ArrayDeque(chain).also {
            it.addAll(next.chain)
        })
    }
    operator fun plus(next: CQCodeMessageItem): CQCodeMessage {
        return CQCodeMessage(ArrayDeque(chain).also {
            it.add(next)
        })
    }

    override fun toString(): String {
        return StringBuilder().also {
            for (item in chain) {
                it.append(item.toString())
            }
        }.toString()
    }
}


fun CQCodeMessageItem.asMessage(): CQCodeMessage {
    return CQCodeMessage(ArrayDeque(listOf(this)))
}

operator fun CQCodeMessage?.plus(next: CQCodeMessage): CQCodeMessage {
    return if (this == null) {
        next
    } else {
        return this + next
    }
}
operator fun CQCodeMessage?.plus(next: CQCodeMessageItem): CQCodeMessage {
    return if (this == null) {
        next.asMessage()
    } else {
        return this + next
    }
}


object CQCodeMessageSerializer: KSerializer<CQCodeMessage> {
    override val descriptor: SerialDescriptor = listSerialDescriptor<CQCodeMessageItem>()

    override fun deserialize(decoder: Decoder): CQCodeMessage {
        try {
            when (decoder) {
                is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
                    is JsonPrimitive -> {
                        if (element.isString) {
                            return deserialize(CQCodeMessageDecoder(element.content, decoder.serializersModule))
                        }
                    }
                    is JsonArray -> {
                        val result = ArrayDeque<CQCodeMessageItem>()
                        for (item in element) {
                            item as JsonObject
                            val type = CQCodeMessageItem.Type.valueOf(item["type"]?.jsonPrimitive!!.content)
                            result.add(type.decodeFromJsonElement(CQCodeJson, item["data"]!!.jsonObject))
                        }
                        return CQCodeMessage(result)
                    }
                    else -> { }
                }
                is CQCodeMessageDecoder -> return decoder.decodeCQCodeMessage()
            }
            throw SerializationException("Unsupported decoder type: ${decoder::class}")
        } catch (e: Exception) {
            throw SerializationException("Failed to decoder content", e)
        }
    }

    override fun serialize(encoder: Encoder, value: CQCodeMessage) {
        try {
            when (encoder) {
                is JsonEncoder -> {
                    encoder.encodeJsonElement(buildJsonArray {
                        for (item in value.chain) {
                            add(buildJsonObject {
                                put("type", JsonPrimitive(item._rawType.name))
                                put("data", item._rawType.encodeToJsonElement(CQCodeJson, item))
                            })
                        }
                    })
                }
                is CQCodeMessageEncoder -> encoder.encodeCQCodeMessage(value)
                else -> throw SerializationException("Unsupported encoder type: ${encoder::class}")
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to encoder content: $value", e)
        }
    }
}