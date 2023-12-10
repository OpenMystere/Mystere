package io.github.mystere.qqsdk.qqapi.data

import io.github.mystere.qqsdk.qqapi.websocket.message.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    @SerialName("user")
    val user: User? = null,
    @SerialName("nick")
    val nick: String,
    @SerialName("roles")
    val roles: List<String>,
    @SerialName("joined_at")
    val joinedAt: Timestamp,
)
@Serializable
data class MemberWithGuildID(
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
)