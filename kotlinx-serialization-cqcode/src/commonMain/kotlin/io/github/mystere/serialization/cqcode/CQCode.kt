package io.github.mystere.serialization.cqcode

import io.github.mystere.core.util.logger
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessage
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageDecoder
import io.github.mystere.serialization.cqcode.raw.CQCodeRawMessageEncoder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.getPolymorphicDescriptors
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

const val CQCodeJsonClassDiscriminator: String = "_cqt"

val CQCodeJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
    useAlternativeNames = true
    useArrayPolymorphism = false
    classDiscriminator = CQCodeJsonClassDiscriminator
}

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
        return CQCodeJson.decodeFromJsonElement<CQCodeRawMessage>(element)
    }
    fun decodeRawFromString(string: String): CQCodeRawMessage {
        try {
            return if (string.startsWith("[{\"") && string.endsWith("\"}]")) {
                CQCodeJson.decodeFromString(CQCodeRawMessage.serializer(), string)
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
        return CQCodeJson.encodeToJsonElement(value).jsonArray
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
