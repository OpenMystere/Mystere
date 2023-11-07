package io.github.mystere.qq.qqapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppAccessTokenReqDto(
    @SerialName("appId")
    val appId: String,
    @SerialName("clientSecret")
    val clientSecret: String,
)

@Serializable
data class AppAccessTokenRespDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
)