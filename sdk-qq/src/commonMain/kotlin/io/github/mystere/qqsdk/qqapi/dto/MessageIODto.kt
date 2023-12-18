package io.github.mystere.qqsdk.qqapi.dto

import io.github.mystere.qqsdk.qqapi.data.MessageArk
import io.github.mystere.qqsdk.qqapi.data.MessageEmbed
import io.github.mystere.qqsdk.qqapi.data.MessageMarkdown
import io.github.mystere.qqsdk.qqapi.data.MessageReference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class GuildMessageRequestDto(
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

@Serializable
data class GroupMessageRequestDto(
    @SerialName("content")
    val content: String? = null,
    @SerialName("msg_type")
    val msgType: Int,
    @SerialName("markdown")
    val markdown: Markdown? = null,
    @SerialName("keyboard")
    val keyboard: Keyboard? = null,
    @SerialName("ark")
    val ark: MessageArk? = null,
    @SerialName("media")
    val media: Media? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("message_reference")
    val messageReference: MessageReference? = null,
    @SerialName("event_id")
    val eventId: String? = null,
    @SerialName("msg_id")
    val msgId: String? = null,
    @SerialName("msg_seq")
    val msgSeq: Int = 1,
) {
    @Serializable
    data class Markdown(
        @SerialName("content")
        val content: String? = null,
        @SerialName("custom_template_id")
        val customTemplateId: String? = null,
        @SerialName("params")
        val params: JsonArray? = null,
    )
    @Serializable
    data class Keyboard(
        @SerialName("id")
        val id: String? = null,
        @SerialName("content")
        val content: KeyboardContent? = null,
    )
    @Serializable
    data class KeyboardContent(
        @SerialName("rows")
        val rows: List<List<KeyboardButton>>
    )
    @Serializable
    data class KeyboardButton(
        @SerialName("id")
        val id: String? = null,
        @SerialName("render_data")
        val renderData: RenderData,
        @SerialName("action")
        val action: Action,
        @SerialName("data")
        val data: String,
        @SerialName("reply")
        val reply: Boolean = false,
        @SerialName("enter")
        val enter: Boolean = false,
        @SerialName("anchor")
        val anchor: Boolean = false,
        @SerialName("click_limit")
        val clickLimit: Int? = null,
        @SerialName("unsupport_tips")
        val unsupportTips: String,
    ) {
        @Serializable
        data class RenderData(
            @SerialName("label")
            val label: String,
            @SerialName("visited_label")
            val visitedLabel: String,
            @SerialName("style")
            val style: Int,
        )
        @Serializable
        data class Action(
            @SerialName("type")
            val type: Int,
            @SerialName("label")
            val label: String,
            @SerialName("visited_label")
            val visitedLabel: String,
            @SerialName("permisson")
            val permission: Permission,
        ) {
            @Serializable
            data class Permission(
                @SerialName("type")
                val type: String,
                @SerialName("specify_role_ids")
                val specifyRoleIds: List<String>? = null,
                @SerialName("specify_user_ids")
                val specifyUserIds: List<String>? = null,
            )
        }
    }
    @Serializable
    data class Media(
        @SerialName("file_info")
        val fileInfo: String,
    )
}
