package io.github.mystere.serialization.cqcode

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

sealed class CQCode(
    override val serializersModule: SerializersModule
): StringFormat {
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        TODO()
    }
    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        TODO()
    }

    companion object: CQCode(
        serializersModule = EmptySerializersModule(),
    ) {

    }
}