package io.github.mystere.qq.v12

import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.qqsdk.qqapi.data.User
import io.github.mystere.qqsdk.qqapi.websocket.message.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object OneBotV12QQEvent {
    @Serializable
    data class GuildEvent(
        @SerialName("description")
        val description: String,
        @SerialName("icon")
        val icon: String,
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
    ): IOneBotEvent.Data

    @Serializable
    data class ChannelEvent(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("name")
        val name: String,
        @SerialName("op_user_id")
        val opUserId: Int,
        @SerialName("owner_id")
        val ownerId: String,
        @SerialName("channel_sub_type")
        val channelSubType: String,
        @SerialName("channel_type")
        val channelType: String,
    ): IOneBotEvent.Data

    @Serializable
    data class GuildMemberEvent(
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
    ): IOneBotEvent.Data

    @Serializable
    data class AudioLiveChannelMemberEvent(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("uschannel_id")
        val channelId: User,
        @SerialName("channel_type")
        val channelType: Int,
        @SerialName("user_id")
        val userId: String,
    ): IOneBotEvent.Data

    @Serializable
    data class GroupRobotEvent(
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("group_openid")
        val groupOpenid: String,
        @SerialName("op_member_openid")
        val opMemberOpenid: String,
    ): IOneBotEvent.Data

    @Serializable
    data class UserRobotEvent(
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("openid")
        val openid: String,
    ): IOneBotEvent.Data
}
