package io.github.mystere.onebot.v12.cqcode

import io.github.mystere.serialization.cqcode.ICQCodeMessageItem
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemEncoder
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemOperator
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

/**
 * CQ 码元素
 * @see <a href="https://12.onebot.dev/interface/message/segments/">消息段 - OneBot 12 标准</a>
 */
@Serializable
sealed class CQCodeV12MessageItem(
    @Transient
    private val _typeEnum: Type = Type.text
): ICQCodeMessageItem, ICQCodeMessageItemOperator<CQCodeV12MessageItem, CQCodeV12Message> {
    // 纯文本
    @Serializable
    data class Text(
        @SerialName("text")
        val text: String
    ): CQCodeV12MessageItem()

    // 提及（即 @）
    @Serializable
    data class Mention(
        @SerialName("user_id")
        val userId: String,
    ): CQCodeV12MessageItem(Type.mention)

    // 提及所有人
    @Serializable
    data object MentionAll: CQCodeV12MessageItem(Type.mention_all)

    // 图片
    @Serializable
    data class Image(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeV12MessageItem(Type.image)

    // 语音
    @Serializable
    data class Voice(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeV12MessageItem(Type.voice)

    // 音频
    @Serializable
    data class Audio(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeV12MessageItem(Type.audio)

    // 音频
    @Serializable
    data class Video(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeV12MessageItem(Type.video)

    // 音频
    @Serializable
    data class File(
        @SerialName("file_id")
        val fileId: String,
    ): CQCodeV12MessageItem(Type.file)

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
    ): CQCodeV12MessageItem(Type.location)

    // 回复
    @Serializable
    data class Reply(
        @SerialName("message_id")
        val messageId: String,
        @SerialName("user_id")
        val userId: String? = null,
    ): CQCodeV12MessageItem(Type.reply)

    // 子频道
    @Serializable
    data class SubChannel(
        @SerialName("id")
        val id: String,
    ): CQCodeV12MessageItem(Type.sub_channel)

    @Transient
    override val _type: String = _typeEnum.name
    override fun ArrayDeque<CQCodeV12MessageItem>.asMessage(): CQCodeV12Message {
        return CQCodeV12Message(this)
    }

    enum class Type(
        override val `class`: KClass<out CQCodeV12MessageItem>
    ): ICQCodeMessageItem.Type {
        text(Text::class),
        mention(Mention::class),
        mention_all(MentionAll::class),
        image(Image::class),
        voice(Voice::class),
        audio(Audio::class),
        video(Video::class),
        file(File::class),
        location(Location::class),
        reply(Reply::class),
        sub_channel(SubChannel::class),
    }
}