package io.github.mystere.qqsdk.qqapi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuildUser(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("union_openid")
    val unionOpenid: String? = null,
    @SerialName("union_user_account")
    val unionUserAccount: String? = null,
)

@Serializable
data class GroupUser(
    @SerialName("member_openid")
    val memberOpenid: String
)

@Serializable
data class C2CUser(
    @SerialName("user_openid")
    val userOpenid: String
)
