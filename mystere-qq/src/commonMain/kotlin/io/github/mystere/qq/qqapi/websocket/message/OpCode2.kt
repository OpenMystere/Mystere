package io.github.mystere.qq.qqapi.websocket.message

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

object OpCode2 {
    @Serializable
    data class IdentifyPayload(
        @SerialName("token")
        val token: String,
        @SerialName("intents")
        val intents: Intent,
        @SerialName("shared")
        val shared: List<Int> = listOf(0, 1),
        @SerialName("properties")
        val properties: JsonObject = buildJsonObject { },
    ): OpCodeData
}

@Serializable(with = Intent.Serializer::class)
open class Intent private constructor(
    val rawIntent: Long
) {
    object GUILDS: Intent(1 shl 0)
    object GUILD_MEMBERS: Intent(1 shl 1)
    object GUILD_MESSAGES: Intent(1 shl 9)
    object GUILD_MESSAGE_REACTIONS: Intent(1 shl 10)
    object DIRECT_MESSAGE: Intent(1 shl 12)
    object OPEN_FORUMS_EVENT: Intent(1 shl 12)
    object AUDIO_OR_LIVE_CHANNEL_MEMBER: Intent(1 shl 19)
    object INTERACTION: Intent(1 shl 26)
    object MESSAGE_AUDIT: Intent(1 shl 27)
    object FORUMS_EVENT: Intent(1 shl 28)
    object AUDIO_ACTION: Intent(1 shl 29)
    object PUBLIC_GUILD_MESSAGES: Intent(1 shl 30)

    operator fun plus(intent: Intent): Intent {
        return Intent(intent.rawIntent + rawIntent)
    }

    companion object {
        fun raw(value: Long): Intent {
            return Intent(value)
        }

        val DEFAULT = GUILDS + PUBLIC_GUILD_MESSAGES + GUILD_MEMBERS
    }

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    object Serializer: KSerializer<Intent> {
        override val descriptor: SerialDescriptor = buildSerialDescriptor("opcode", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Intent {
            return Intent(decoder.decodeLong())
        }

        override fun serialize(encoder: Encoder, value: Intent) {
            encoder.encodeLong(value.rawIntent)
        }
    }
}
