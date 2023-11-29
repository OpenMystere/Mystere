package io.github.mystere.qqsdk.qqapi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String,
    @SerialName("username")
    val username: String,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("bot")
    val bot: Boolean,
    @SerialName("union_openid")
    val unionOpenid: String,
    @SerialName("union_user_account")
    val unionUserAccount: String,
)
