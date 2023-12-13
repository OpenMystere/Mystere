package io.github.mystere.qqsdk.qqapi.dto

import io.github.mystere.qqsdk.qqapi.data.EmojiType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionDto(
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("message_id")
    val messageId: String,
    @SerialName("type")
    val type: EmojiType,
    @SerialName("id")
    val id: String,
)