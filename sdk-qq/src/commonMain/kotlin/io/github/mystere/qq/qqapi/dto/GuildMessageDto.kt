package io.github.mystere.qq.qqapi.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MessageEmbed(
    @SerialName("title")
    val title: String,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("thumbnail")
    val thumbnail: MessageEmbedThumbnail,
    @SerialName("fields")
    val fields: List<MessageEmbedField>,
)
@Serializable
data class MessageEmbedThumbnail(
    @SerialName("url")
    val url: String,
)
@Serializable
data class MessageEmbedField(
    @SerialName("name")
    val name: String,
)
@Serializable
data class MessageArk(
    @SerialName("template_id")
    val templateId: Int,
    @SerialName("kv")
    val kv: List<MessageArkKv>
)
@Serializable
data class MessageArkKv(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String,
    @SerialName("obj")
    val obj: List<MessageArkObj>,
)
@Serializable
data class MessageArkObj(
    @SerialName("obj_kv")
    val objKv: List<MessageArkObjKv>,
)
@Serializable
data class MessageArkObjKv(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String,
)
@Serializable
data class MessageReference(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("ignore_get_message_error")
    val ignoreGetMessageError: Boolean = false,
)
@Serializable
data class MessageMarkdown(
    @SerialName("key")
    val key: String,
    @SerialName("values")
    val values: List<String>,
)