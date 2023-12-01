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
    data class GuildMessage(
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
        val editedTimestamp: Timestamp? = null,
        @SerialName("mention_everyone")
        val mentionEveryone: Boolean? = null,
        @SerialName("author")
        val author: GuildUser,
        @SerialName("attachments")
        val attachments: List<MessageAttachment> = emptyList(),
        @SerialName("embeds")
        val embeds: List<MessageEmbed> = emptyList(),
        @SerialName("mentions")
        val mentions: List<GuildUser> = emptyList(),
        @SerialName("member")
        val member: Member? = null,
        @SerialName("ark")
        val ark: List<MessageArk> = emptyList(),
        @SerialName("seq")
        val seq: Int? = null,
        @SerialName("seq_in_channel")
        val seqInChannel: Int,
        @SerialName("message_reference")
        val messageReference: MessageReference? = null,
    ): OpCodeData

    @Serializable
    data class GroupMessage(
        @SerialName("id")
        val id: String,
        @SerialName("GroupMessage")
        val groupOpenid: String,
        @SerialName("content")
        val content: String,
        @SerialName("timestamp")
        val timestamp: Timestamp,
        @SerialName("author")
        val author: GroupUser,
        @SerialName("attachments")
        val attachments: List<MessageAttachment> = emptyList(),
    ): OpCodeData

    @Serializable
    data class C2CMessage(
        @SerialName("id")
        val id: String,
        @SerialName("content")
        val content: String,
        @SerialName("timestamp")
        val timestamp: Timestamp,
        @SerialName("author")
        val author: C2CUser,
        @SerialName("attachments")
        val attachments: List<MessageAttachment> = emptyList(),
    ): OpCodeData

    @Serializable
    data class GroupAddRobot(
        @SerialName("timestamp")
        val timestamp: Timestamp,
        @SerialName("group_openid")
        val groupOpenid: Timestamp,
        @SerialName("op_member_openid")
        val opMemberOpenid: Timestamp,
    )
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
