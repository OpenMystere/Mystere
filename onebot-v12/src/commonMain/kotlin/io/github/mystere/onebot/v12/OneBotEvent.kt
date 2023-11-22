package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.serialization.cqcode.CQCodeMessage
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IOneBotV12Event: IOneBotEvent {
    @SerialName("post_type")
    val postType: PostType
    @SerialName("self_id")
    val selfId: String

    @SerialName("time")
    val time: Long

    enum class PostType {
        message, notice, request, meta_event;
    }


    /************ 消息（message） ***********/

    enum class MessageType {
        private, group, guild,
    }
    enum class MessageSubType {
        friend, group, channel, other
    }
    enum class Sex {
        male, female, unknown
    }

    // 私聊消息
    @Serializable
    data class MessagePrivate(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: MessageSubType,
        @SerialName("message_id")
        val messageId: Int,
        @SerialName("message")
        val message: CQCodeMessage,
        @SerialName("raw_message")
        val rawIMessage: String,
        @SerialName("font")
        val font: Int,
        @SerialName("sender")
        val sender: Sender,
        @SerialName("user_id")
        val userId: Long = sender.userId,
    ): IOneBotV12Event {
        @SerialName("message_type")
        val messageType: MessageType = MessageType.private
        @SerialName("post_type")
        override val postType: IOneBotV12Event.PostType = IOneBotV12Event.PostType.message
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds()

        @Serializable
        data class Sender(
            @SerialName("user_id")
            val userId: Long,
            @SerialName("nickname")
            val nickname: String? = null,
            @SerialName("sex")
            val sex: Sex? = null,
            @SerialName("age")
            val age: Int? = null,
        )
    }

    // 消息
    @Serializable
    data class Message(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("message_type")
        val messageType: MessageType = MessageType.group,
        @SerialName("sub_type")
        val subType: MessageSubType,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("message")
        val message: CQCodeMessage,
        @SerialName("raw_message")
        val rawMessage: String,
        @SerialName("font")
        val font: Int? = null,
        @SerialName("sender")
        val sender: Sender,
        @SerialName("user_id")
        val userId: String,
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
    ): IOneBotV12Event {
        @SerialName("post_type")
        override val postType: IOneBotV12Event.PostType = IOneBotV12Event.PostType.message
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds()

        @Serializable
        data class Sender(
            @SerialName("user_id")
            val userId: String,
            @SerialName("tiny_id")
            val tinyId: String? = null,
            @SerialName("card")
            val card: String? = null,
            @SerialName("nickname")
            val nickname: String? = null,
            @SerialName("sex")
            val sex: Sex? = null,
            @SerialName("age")
            val age: Int? = null,
        )
    }


    /************ 元事件（meta） ***********/

    enum class MetaEventType {
        lifecycle, heartbeat
    }

    // 生命周期
    enum class LifecycleSubType {
        enable, disable, connect
    }
    @Serializable
    data class MetaLifecycle(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: LifecycleSubType,
    ): IOneBotV12Event {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.lifecycle
        @SerialName("post_type")
        override val postType: IOneBotV12Event.PostType = IOneBotV12Event.PostType.meta_event
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds()
    }

    // 心跳
    @Serializable
    data class HeartbeatStatus(
        @SerialName("online")
        val online: Boolean?,
        @SerialName("good")
        val good: Boolean,
    )
    @Serializable
    data class MetaHeartbeat(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("state")
        val state: HeartbeatStatus,
    ): IOneBotV12Event {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.heartbeat
        @SerialName("post_type")
        override val postType: IOneBotV12Event.PostType = IOneBotV12Event.PostType.meta_event
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds()
    }


    /************ 通知事件（notice） ***********/

    enum class NoticeType {
        group_upload
    }

    // 群文件上传
    @Serializable
    data class FileMeta(
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String,
        @SerialName("size")
        val size: Long,
        @SerialName("busid")
        val busid: Long,
    )
    @Serializable
    data class NoticeGroupFileUpload(
        override val selfId: String,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("file")
        val file: FileMeta,
    ): IOneBotV12Event {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_upload
        override val postType: IOneBotV12Event.PostType = IOneBotV12Event.PostType.notice
        override val time: Long = Clock.System.now().toEpochMilliseconds()
    }
}