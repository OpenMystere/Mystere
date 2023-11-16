package io.github.mystere.serialization.cqcode

import io.github.mystere.util.logger
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

internal val CQCodeJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
}

sealed class CQCode(
    override val serializersModule: SerializersModule
): StringFormat {
    private val log by logger()

    @Deprecated(
        message = "Use decodeFromString instead.",
        replaceWith = ReplaceWith("decodeFromString(string)"),
        level = DeprecationLevel.HIDDEN,
    )
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return decodeFromString(string) as T
    }

    fun decodeFromJson(element: JsonArray): CQCodeMessage {
        val result = ArrayDeque<CQCodeMessageItem>()
        for (item in element) {
            result.addLast(decodeSingleItemFromJson(item.jsonObject))
        }
        return CQCodeMessage(result)
    }
    fun decodeFromString(string: String): CQCodeMessage {
        try {
            if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
                return decodeFromJson(
                    CQCodeJson.decodeFromString(JsonArray.serializer(), string)
                )
            } else {
                val result = ArrayDeque<CQCodeMessageItem>()

                var index = 0
                val chars = string.toCharArray()
                while (index < chars.size) {
                    when (chars[index]) {
                        '[' -> {
                            val startIndex = index
                            try {
                                while (chars[index] != ']') {
                                    index += 1
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                throw SerializationException("Not a valid CQCode: $string")
                            }
                            index += 1
                            result.addLast(decodeSingleItemFromString(
                                string.substring(startIndex, index)
                            ))
                        }
                        else -> {
                            val startIndex = index
                            try {
                                while (chars[index] != '[') {
                                    index += 1
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                throw SerializationException("Not a valid CQCode: $string")
                            }
                            result.addLast(CQCodeMessageItem.Text(
                                string.substring(startIndex, index)
                            ))
                            index += 1
                        }
                    }
                }
                return CQCodeMessage(result)
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to decode content: $string", e)
        }
    }

    fun decodeSingleItemFromJson(element: JsonObject): CQCodeMessageItem {
        try {
            val type = element["type"]?.jsonPrimitive!!.content.asCQCodeMessageItemType
            return type.decodeFromJsonElement(CQCodeJson, element["data"]!!.jsonObject)
        } catch (e: Exception) {
            throw SerializationException("Failed to decode element: $element", e)
        }
    }
    fun decodeSingleItemFromString(string: String): CQCodeMessageItem {
        try {
            if (string.startsWith("{\"") && string.endsWith("\"}")) {
                return decodeSingleItemFromJson(
                    CQCodeJson.decodeFromString(JsonObject.serializer(), string)
                )
            } else if (string.contains("\\[(.*?)]".toRegex())) {
                if (!string.startsWith("[CQ:") || !string.endsWith("]")) {
                    throw SerializationException("Not a valid CQCode item: $string")
                }
                val type = string.substring(
                    4, if (string.contains(",")) {
                        string.indexOf(",")
                    } else {
                        string.length - 4
                    })
                    .takeIf { it != "text" }
                    ?.lowercase()
                    ?: throw SerializationException("Not a valid CQCode: $string")
                val args = with(string.substring(type.length + 5, string.length - 1)) {
                    HashMap<String, String>().also {
                        it["type"] = type
                        for (item in split(",")) {
                            if (!item.contains("=")) {
                                continue
                            }
                            val keyValue = item.split("=")
                            it[keyValue[0]] = keyValue[1]
                        }
                    }
                }
                return type.asCQCodeMessageItemType.deserialize(CQCodeMessageItemDecoder(
                    string, args, serializersModule
                ))
            } else {
                return CQCodeMessageItem.Text(string)
            }
        } catch (e: Exception) {
            throw SerializationException("Failed to decode content: $string", e)
        }
    }


    @Deprecated(
        message = "Use encodeToString instead.",
        replaceWith = ReplaceWith("encodeToString(string)"),
        level = DeprecationLevel.HIDDEN,
    )
    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return encodeToString(value as CQCodeMessage)
    }
    fun encodeToJson(value: CQCodeMessage): JsonArray {
        return buildJsonArray {
            for (item in value.chain) {
                add(encodeSingleItemToJson(item))
            }
        }
    }
    fun encodeToString(value: CQCodeMessage): String {
        return StringBuilder().also {
            for (item in value.chain) {
                it.append(encodeSingleItemToString(item))
            }
        }.toString()
    }
    fun encodeSingleItemToJson(element: CQCodeMessageItem): JsonObject {
        return CQCodeJson.encodeToJsonElement(element).jsonObject
    }
    fun <T: CQCodeMessageItem> encodeSingleItemToString(value: T): String {
        when (value) {
            is CQCodeMessageItem.Text -> return value.text
            else -> {
                val encoder = CQCodeMessageItemEncoder(serializersModule)
                value.type.serialize(encoder, value)
                return encoder.encodeFinalResult()
            }
        }
    }

    companion object: CQCode(
        serializersModule = EmptySerializersModule(),
    ) {

    }
}
