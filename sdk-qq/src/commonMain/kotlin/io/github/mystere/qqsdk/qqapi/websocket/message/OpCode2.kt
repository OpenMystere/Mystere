package io.github.mystere.qqsdk.qqapi.websocket.message

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

enum class SingleIntent(
    val rawIntent: Long
) {
    GUILDS(1 shl 0),
    GUILD_MEMBERS(1 shl 1),
    GUILD_MESSAGES(1 shl 9),
    GUILD_MESSAGE_REACTIONS(1 shl 10),
    DIRECT_MESSAGE(1 shl 12),
    OPEN_FORUMS_EVENT(1 shl 12),
    AUDIO_OR_LIVE_CHANNEL_MEMBER(1 shl 19),
    INTERACTION(1 shl 26),
    MESSAGE_AUDIT(1 shl 27),
    FORUMS_EVENT(1 shl 28),
    AUDIO_ACTION(1 shl 29),
    PUBLIC_GUILD_MESSAGES(1 shl 30),

    _DEFAULT((GUILDS + PUBLIC_GUILD_MESSAGES + GUILD_MEMBERS).rawIntent),
    _PRIVATE((GUILD_MESSAGES + FORUMS_EVENT).rawIntent),
    ;

    operator fun plus(intent: SingleIntent): Intent {
        return Intent(intent.rawIntent + rawIntent)
    }
}

fun List<SingleIntent>.asIntent(): Intent {
    if (isEmpty()) {
        throw IndexOutOfBoundsException("Empty intent list cannot convert to Intent")
    }
    var result: Intent? = null
    for (intent in this) {
        if (result == null) {
            result = Intent.raw(intent.rawIntent)
        } else {
            result += intent
        }
    }
    return result!!
}

@Serializable(with = Intent.Serializer::class)
open class Intent internal constructor(
    val rawIntent: Long
) {
    operator fun plus(intent: SingleIntent): Intent {
        return Intent(intent.rawIntent + rawIntent)
    }

    companion object {
        fun raw(value: Long): Intent {
            return Intent(value)
        }
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
