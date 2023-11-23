package io.github.mystere.onebot.v11

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OneBotV11Referer(
    @SerialName("origin_message_id")
    val originMessageId: String? = null
)