package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.logger
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessage
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageDecoder
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageEncoder
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

fun CQCode(block: CQCode.MutableConfig.() -> Unit): CQCode {
    return CQCode(CQCode.MutableConfig().also(block))
}

open class CQCode(
    val config: Config,
): StringFormat {
    override val serializersModule: SerializersModule = config.serializersModule
    interface Config {
        val serializersModule: SerializersModule
    }
    data class MutableConfig(
        override var serializersModule: SerializersModule = EmptySerializersModule(),
    ): Config

    private val log by logger()

    @Deprecated(
        message = "Use decodeFromString instead.",
        replaceWith = ReplaceWith("decodeFromString(string)"),
        level = DeprecationLevel.HIDDEN,
    )
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return decodeRawFromString(string) as T
    }

    fun decodeRawFromJson(element: JsonArray): CQCodeRawMessage {
        return MystereJson.decodeFromJsonElement<CQCodeRawMessage>(element)
    }
    fun decodeRawFromString(string: String): CQCodeRawMessage {
        try {
            return if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
                MystereJson.decodeFromString(CQCodeRawMessage.serializer(), string)
            } else {
                CQCodeRawMessage.serializer()
                    .deserialize(CQCodeRawMessageDecoder(string, serializersModule))
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
        return encodeToString(value as CQCodeRawMessage)
    }
    fun encodeToJson(value: CQCodeRawMessage): JsonArray {
        return MystereJson.encodeToJsonElement(value).jsonArray
    }
    fun encodeToString(value: CQCodeRawMessage): String {
        val encoder = CQCodeRawMessageEncoder(serializersModule)
        encoder.encodeCQCodeMessage(value)
        return encoder.encodeFinalResult()
    }

    companion object: CQCode(
        config = MutableConfig()
    )
}
