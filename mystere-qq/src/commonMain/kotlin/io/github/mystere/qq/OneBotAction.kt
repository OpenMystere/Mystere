package io.github.mystere.qq

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.qqsdk.qqapi.data.EmojiType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OneBotQQAction: IOneBotAction.Action {
    rich_media_group,
    rich_media_c2c,
    message_reaction_put,
    message_reaction_delete,
    message_reaction_get,
}

sealed interface OneBotQQActionParam: IOneBotAction.Param {
    @Serializable
    data class RichMedia(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("emoji_type")
        val emojiType: EmojiType,
        @SerialName("id")
        val id: String,
    ): OneBotQQActionParam
    @Serializable
    data class MessageReaction(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("emoji_type")
        val emojiType: EmojiType,
        @SerialName("id")
        val id: String,
    ): OneBotQQActionParam
}
