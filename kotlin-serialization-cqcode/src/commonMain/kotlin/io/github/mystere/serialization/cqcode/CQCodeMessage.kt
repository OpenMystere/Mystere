package io.github.mystere.serialization.cqcode

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
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
        return with(StringBuilder()) {
            for (item in chain) {
                append(item.toString())
            }
        }.toString()
    }
}

object CQCodeMessageSerializer: KSerializer<CQCodeMessage> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "io.github.mystere.serializer.cqcode.CQCodeMessage"
    ) {
    }

    override fun deserialize(decoder: Decoder): CQCodeMessage {
        try {
            when (decoder) {
                is JsonDecoder -> {
                    val jsonObj = decoder.decodeJsonElement().jsonObject
                    return CQCodeJson.decodeFromJsonElement(
                        jsonObj["type"]!!.jsonPrimitive.content.cqCodeSerializer(),
                        jsonObj["data"]!!.jsonObject,
                    )
                }
                is CQCodeMessageItemDecoder -> {
                    val string = decoder.decodeString()
                    if (string.contains("[(.*?)]".toRegex())) {
                        if (!string.startsWith("[CQ:") || !string.endsWith("]")) {
                            throw SerializationException("Not a valid CQCode: $string")
                        }
                        val typeEndIndex = if (string.contains(",")) {
                            string.indexOf(",")
                        } else {
                            string.length - 4
                        }
                        return CQCode.decodeFromString(
                            string.substring(4, typeEndIndex).cqCodeSerializer(),
                            string,
                        )
                    } else {
                        return CQCodeMessageItem.RawText(string)
                    }
                }
                else -> throw SerializationException("Unsupported decoder type: ${decoder::class}")
            }
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
                            buildJsonObject {
                                put("type", JsonPrimitive(item.type))
                                put("data", CQCodeJson.encodeToJsonElement(value))
                            }
                        }
                    })
                }
                is CQCodeMessageItemEncoder -> {
                    encoder.encodeString(with(StringBuilder()) {
                        for (item in value.chain) {
                            append(CQCode.encodeToString(item.type.cqCodeSerializer(), item))
                        }
                    }.toString())
                }
                else -> throw SerializationException("Unsupported encoder type: ${encoder::class}")
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to encoder content: $value", e)
        }
    }
}
