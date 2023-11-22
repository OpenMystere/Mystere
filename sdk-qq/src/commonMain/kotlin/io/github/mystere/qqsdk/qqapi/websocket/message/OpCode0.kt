package io.github.mystere.qqsdk.qqapi.websocket.message

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
    data class AtMessageCreate(
        @SerialName("attachments")
        val attachments: List<Attachment> = emptyList(),
        @SerialName("author")
        val author: Author,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("content")
        val content: String,
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("id")
        val id: String,
        @SerialName("member")
        val member: Member,
        @SerialName("mentions")
        val mentions: List<Mentions>,
        @SerialName("seq")
        val seq: Int,
        @SerialName("seq_in_channel")
        val seqInChannel: String,
        @SerialName("timestamp")
        val timestamp: Timestamp,
    ): OpCodeData {
        @Serializable
        data class Attachment(
            @SerialName("id")
            val id: String,
            @SerialName("content_type")
            val contentType: String,
            @SerialName("filename")
            val filename: String,
            @SerialName("url")
            val url: String,
            @SerialName("height")
            val height: Int? = null,
            @SerialName("width")
            val width: Int? = null,
            @SerialName("size")
            val size: Long? = null,
        )
        @Serializable
        data class Author(
            @SerialName("avatar")
            val avatar: String,
            @SerialName("bot")
            val bot: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("username")
            val username: String,
        )
        @Serializable
        data class Member(
            @SerialName("joined_at")
            val joinedAt: Timestamp,
            @SerialName("nick")
            val nick: String,
            @SerialName("roles")
            val roles: List<Int>,
        )
        @Serializable
        data class Mentions(
            @SerialName("avatar")
            val avatar: String,
            @SerialName("bot")
            val bot: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("username")
            val username: String,
        )
    }
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
