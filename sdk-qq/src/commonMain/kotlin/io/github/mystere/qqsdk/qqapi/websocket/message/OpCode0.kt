package io.github.mystere.qqsdk.qqapi.websocket.message

import io.github.mystere.qqsdk.qqapi.data.*
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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
    data class GuildInfo(
        @SerialName("description")
        val description: String,
        @SerialName("icon")
        val icon: String,
        @SerialName("id")
        val id: String,
        @SerialName("joined_at")
        val joinedAt: Timestamp,
        @SerialName("max_members")
        val maxMembers: Int,
        @SerialName("member_count")
        val memberCount: Int,
        @SerialName("name")
        val name: String,
        @SerialName("op_user_id")
        val opUserId: String,
        @SerialName("owner_id")
        val ownerId: String,
    ): OpCodeData

    @Serializable
    data class ChannelInfo(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String,
        @SerialName("op_user_id")
        val opUserId: Int,
        @SerialName("owner_id")
        val ownerId: String,
        @SerialName("sub_type")
        val subType: String,
        @SerialName("type")
        val type: String,
    ): OpCodeData

    @Serializable
    data class GuildMember(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("user")
        val user: User,
        @SerialName("nick")
        val nick: String,
        @SerialName("roles")
        val roles: List<String>,
        @SerialName("joined_at")
        val joinedAt: Timestamp,
        @SerialName("op_user_id")
        val opUserId: Int,
    ): OpCodeData

    @Serializable
    data class AudioLiveChannelMember(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("uschannel_id")
        val channelId: User,
        // 2-音视频子频道 5-直播子频道
        @SerialName("channel_type")
        val channelType: Int,
        @SerialName("user_id")
        val userId: String,
    ): OpCodeData

    @Serializable
    data class GroupRobot(
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("group_openid")
        val groupOpenid: String,
        @SerialName("op_member_openid")
        val opMemberOpenid: String,
    ): OpCodeData

    @Serializable
    data class UserRobot(
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("openid")
        val openid: String,
    ): OpCodeData

    @Serializable
    data class MessageReaction(
        @SerialName("user_id")
        val userId: String,
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("target")
        val target: ReactionTarget,
        @SerialName("emoji")
        val emoji: Emoji,
    ): OpCodeData
}

typealias Timestamp = @Serializable(with = TimestampSerializer::class) Long
object TimestampSerializer: KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): Timestamp {
        val value = (decoder as JsonDecoder).decodeJsonElement().jsonPrimitive
        return value.longOrNull ?: Instant.parse(value.content).toEpochMilliseconds()
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
//        encoder.encodeString(Instant.fromEpochMilliseconds(value)
//            .toLocalDateTime(TimeZone.currentSystemDefault())
//            .toString())
        encoder.encodeLong(value)
    }
}
