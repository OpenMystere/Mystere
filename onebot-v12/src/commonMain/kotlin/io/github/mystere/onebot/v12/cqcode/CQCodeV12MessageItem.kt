package io.github.mystere.onebot.v12.cqcode

import io.github.mystere.serialization.cqcode.CQCodeMessageItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * CQ 码元素
 * @see <a href="https://12.onebot.dev/interface/message/segments/">消息段 - OneBot 12 标准</a>
 */
object CQCodeV12MessageItem {
    // 提及（即 @）
    @Serializable
    data class Mention(
        @SerialName("user_id")
        val userId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Mention)

    // 提及所有人
    @Serializable
    data object MentionAll: CQCodeMessageItem(CQCodeV12MessageItem.Type.MentionAll)

    // 图片
    @Serializable
    data class Image(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Image)

    // 语音
    @Serializable
    data class Voice(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Voice)

    // 音频
    @Serializable
    data class Audio(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Audio)

    // 音频
    @Serializable
    data class Video(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Video)

    // 音频
    @Serializable
    data class File(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.File)

    // 位置
    @Serializable
    data class Location(
        @SerialName("latitude")
        val latitude: Float,
        @SerialName("longitude")
        val longitude: Float,
        @SerialName("title")
        val title: String? = null,
        @SerialName("content")
        val content: String? = null,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Location)

    // 回复
    @Serializable
    data class Reply(
        @SerialName("message_id")
        val messageId: String,
        @SerialName("user_id")
        val userId: String? = null,
    ): CQCodeMessageItem(CQCodeV12MessageItem.Type.Reply)


    object Type {
        object Mention: CQCodeMessageItem.Type<CQCodeV12MessageItem.Mention>(CQCodeV12MessageItem.Mention.serializer())
        object MentionAll: CQCodeMessageItem.Type<CQCodeV12MessageItem.MentionAll>(CQCodeV12MessageItem.MentionAll.serializer())
        object Image: CQCodeMessageItem.Type<CQCodeV12MessageItem.Image>(CQCodeV12MessageItem.Image.serializer())
        object Voice: CQCodeMessageItem.Type<CQCodeV12MessageItem.Voice>(CQCodeV12MessageItem.Voice.serializer())
        object Audio: CQCodeMessageItem.Type<CQCodeV12MessageItem.Audio>(CQCodeV12MessageItem.Audio.serializer())
        object Video: CQCodeMessageItem.Type<CQCodeV12MessageItem.Video>(CQCodeV12MessageItem.Video.serializer())
        object File: CQCodeMessageItem.Type<CQCodeV12MessageItem.File>(CQCodeV12MessageItem.File.serializer())
        object Location: CQCodeMessageItem.Type<CQCodeV12MessageItem.Location>(CQCodeV12MessageItem.Location.serializer())
        object Reply: CQCodeMessageItem.Type<CQCodeV12MessageItem.Reply>(CQCodeV12MessageItem.Reply.serializer())
    }
}
