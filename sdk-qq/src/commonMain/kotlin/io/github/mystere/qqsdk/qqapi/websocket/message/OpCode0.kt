package io.github.mystere.qqsdk.qqapi.websocket.message

import io.github.mystere.qqsdk.qqapi.data.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OpCode0 {
    @Serializable
    data class Ready(
        @SerialName("version")
        val version: Int,
        @SerialName("session_id")
        val seasonId: String,
        @SerialName("user")
        val user: User,
    ) {
        @Serializable
        data class User(
            @SerialName("id")
            val id: String,
            @SerialName("username")
            val username: String,
            @SerialName("bot")
            val bot: Boolean,
            @SerialName("status")
            val status: Int,
        )
    }

    @Serializable
    data class Message(
        @SerialName("id")
        val id: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("content")
        val content: String,
        @SerialName("timestamp")
        val timestamp: Timestamp,
        @SerialName("editedTimestamp")
        val editedTimestamp: Timestamp,
        @SerialName("mention_everyone")
        val mentionEveryone: Boolean,
        @SerialName("author")
        val author: User,
        @SerialName("attachments")
        val attachments: List<MessageAttachment>,
        @SerialName("embeds")
        val embeds: List<MessageEmbed>,
        @SerialName("mentions")
        val mentions: List<User>,
        @SerialName("member")
        val member: List<Member>,
        @SerialName("ark")
        val ark: List<MessageArk>,
        @SerialName("seq")
        val seq: Int,
        @SerialName("seq_in_channel")
        val seqInChannel: Int,
        @SerialName("message_reference")
        val messageReference: MessageReference,
    ): OpCodeData
}

typealias Timestamp = @Serializable(with = TimestampSerializer::class) Long
object TimestampSerializer: KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): Timestamp {
        return Instant.parse(decoder.decodeString()).toEpochMilliseconds()
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(Instant.fromEpochMilliseconds(value)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString())
    }
}
