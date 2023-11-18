package io.github.mystere.serialization.cqcode

import io.github.mystere.util.logger
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

internal val CQCodeJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
    useAlternativeNames = true
    useArrayPolymorphism = false
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
        return CQCodeJson.decodeFromJsonElement<CQCodeMessage>(element)
    }
    fun decodeFromString(string: String): CQCodeMessage {
        try {
            if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
                return CQCodeJson.decodeFromString(CQCodeMessage.serializer(), string)
            } else {
                return CQCodeMessage.serializer()
                    .deserialize(CQCodeMessageDecoder(string, serializersModule))
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
        return CQCodeJson.encodeToJsonElement(value).jsonArray
    }
    fun encodeToString(value: CQCodeMessage): String {
        val encoder = CQCodeMessageEncoder(serializersModule)
        encoder.encodeCQCodeMessage(value)
        return encoder.encodeFinalResult()
    }

    companion object: CQCode(
        serializersModule = EmptySerializersModule(),
    ) {

    }
}
