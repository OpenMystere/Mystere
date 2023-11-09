package io.github.mystere.serialization.cqcode

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

/**
 * CQ 码元素
 * @see <a href="https://docs.go-cqhttp.org/cqcode/#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B">CQ 码 / CQ Code | go-cqhttp 帮助中心</a>
 */
@Serializable(with = CQCodeMessageItemSerializer::class)
sealed interface CQCodeMessageItem {
    val type: String

    // 纯文本
    @Serializable
    data class RawText(
        private val text: String
    ): CQCodeMessageItem {
        override val type: String = "raw"
    }

    // QQ 表情
    @Serializable
    data class Face(
        @SerialName("id")
        val id: Long,
    ): CQCodeMessageItem {
        override val type: String = "face"
    }

    // 语音
    @Serializable
    data class Record(
        @SerialName("file")
        val file: String,
        @SerialName("magic")
        val magic: Int = 0,
        @SerialName("url")
        val url: String? = null,
        @SerialName("cache")
        val cache: Int? = null,
    ): CQCodeMessageItem {
        override val type: String = "record"
    }

    // 短视频
    @Serializable
    data class Video(
        @SerialName("file")
        val file: String,
    ): CQCodeMessageItem {
        override val type: String = "video"
    }
}

fun buildCQCodeString(type: String, vararg args: Pair<String, Any?>): String {
    return with(StringBuilder()) {
        append("[CQ:${type}")
        for ((key, value) in args) {
            if (value == null) {
                continue
            }
            append(",${key}=${value.toString().replaceFittedCQ()}")
        }
        append("]")
    }.toString()
}

fun String.replaceFittedCQ(): String {
    return replace("&", "&amp;")
        .replace("[", "&#91;")
        .replace("]", "&#93;")
        .replace(",", "&#44;")
}

internal val CQCodeJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
}
object CQCodeMessageItemSerializer: KSerializer<CQCodeMessageItem> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "io.github.mystere.serializer.cqcode.CQCodeMessageItem"
    ) {
        element<String>("type")
    }

    override fun deserialize(decoder: Decoder): CQCodeMessageItem {
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
                        val type = string.substring(4, if (string.contains(",")) {
                            string.indexOf(",")
                        } else {
                            string.length - 4
                        })
                        if (type == "raw") {
                            throw SerializationException("Not a valid CQCode: $string")
                        }
                        val args = with(string.substring(type.length + 4, string.length - 1)) {
                            return@with HashMap<String, String>().also {
                                for (item in split(",")) {
                                    if (!item.contains("=")) {
                                        continue
                                    }
                                    val keyValue = item.split("=")
                                    it[keyValue[0]] = keyValue[1]
                                }
                            }
                        }
                        with(type.cqCodeSerializer()) {
                            decoder.decodeStructure(descriptor) {

                            }
                        }
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

    override fun serialize(encoder: Encoder, value: CQCodeMessageItem) {
        try {
            when (encoder) {
                is JsonEncoder -> {
                    encoder.encodeJsonElement(buildJsonObject {
                        put("type", JsonPrimitive(value.type))
                        put("data", CQCodeJson.encodeToJsonElement(value))
                    })
                }
                is CQCodeMessageItemEncoder -> {
                    with(value.type.cqCodeSerializer()) {
                        encoder.encodeStructure(descriptor) {

                        }
                    }
                }
                else -> throw SerializationException("Unsupported encoder type: ${encoder::class}")
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to encoder content: $value", e)
        }
    }
}

fun String.cqCodeSerializer(): KSerializer<out CQCodeMessageItem> {
    return when (this) {
        "face" -> CQCodeMessageItem.Face.serializer()
        else -> throw SerializationException("Unknown cq code type: $this")
    }
}
