package io.github.mystere.serialization.cqcode

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = CQCodeMessageSerializer::class)
data class CQCodeMessage internal constructor(
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

object CQCodeMessageSerializer: KSerializer<CQCodeMessage> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "io.github.mystere.serializer.cqcode.CQCodeMessage",
    ) {
    }

    override fun deserialize(decoder: Decoder): CQCodeMessage {
        try {
            if (decoder is JsonDecoder) {
                when (val element = decoder.decodeJsonElement()) {
                    is JsonPrimitive -> {
                        if (element.isString) {
                            return CQCode.decodeFromString(element.content)
                        }
                    }
                    is JsonArray -> {
                        return CQCode.decodeFromJson(element)
                    }
                    else -> { }
                }
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
                            add(CQCodeJson.encodeToJsonElement(item))
                        }
                    })
                }
                else -> throw SerializationException("Unsupported encoder type: ${encoder::class}")
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to encoder content: $value", e)
        }
    }
}
