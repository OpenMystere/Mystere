package io.github.mystere.qqsdk.qqapi.dto

import io.github.mystere.qqsdk.qqapi.data.MessageArk
import io.github.mystere.qqsdk.qqapi.data.MessageEmbed
import io.github.mystere.qqsdk.qqapi.data.MessageMarkdown
import io.github.mystere.qqsdk.qqapi.data.MessageReference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRequestDto(
    @SerialName("content")
    val content: String? = null,
    @SerialName("embed")
    val embed: MessageEmbed? = null,
    @SerialName("ark")
    val ark: MessageArk? = null,
    @SerialName("message_reference")
    val messageReference: MessageReference? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("msg_id")
    val msgId: String? = null,
    @SerialName("event_id")
    val eventId: String? = null,
    @SerialName("markdown")
    val markdown: MessageMarkdown? = null,
)
