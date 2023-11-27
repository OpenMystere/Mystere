package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IOneBotV11Event: IOneBotEvent {
    @SerialName("id")
    val id: String?
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
        val message: CQCodeV11Message,
        @SerialName("raw_message")
        val rawIMessage: String,
        @SerialName("font")
        val font: Int,
        @SerialName("sender")
        val sender: Sender,
        @SerialName("user_id")
        val userId: String,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("message_type")
        val messageType: MessageType = MessageType.private
        @SerialName("post_type")
        override val postType: PostType = PostType.message
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
        val message: CQCodeV11Message,
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
        @SerialName("self_tiny_id")
        val selfTinyId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        @SerialName("anonymous")
        val anonymous: Anonymous? = null,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.message

        @Serializable
        data class Anonymous(
            @SerialName("id")
            val id: String,
            @SerialName("name")
            val name: String,
            @SerialName("flag")
            val flag: String,
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
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.lifecycle
        @SerialName("post_type")
        override val postType: PostType = PostType.meta_event
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
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.heartbeat
        @SerialName("post_type")
        override val postType: PostType = PostType.meta_event
    }


    /************ 通知事件（notice） ***********/

    enum class NoticeType {
        group_upload,
        group_admin,
        group_decrease, group_increase,
        group_ban,
        friend_add,
        friend_recall,
        poke,
        notify,
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
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("file")
        val file: FileMeta,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_upload
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
    }

    // 群管理员变动
    data class NoticeGroupAdminChange(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_admin

        enum class SubType {
            set, unset
        }
    }

    // 群成员减少
    data class NoticeGroupMemberDecrease(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("operator_id")
        val operatorId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_decrease

        enum class SubType {
            leave, kick, kick_me
        }
    }

    // 群成员增加
    data class NoticeGroupMemberIncrease(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("operator_id")
        val operatorId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_increase

        enum class SubType {
            leave, kick, kick_me
        }
    }

    // 群禁言
    data class NoticeGroupBan(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("operator_id")
        val operatorId: Long,
        @SerialName("duration")
        val duration: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_ban

        enum class SubType {
            ban, lift_ban
        }
    }

    // 群禁言
    data class NoticeFriendAdd(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.friend_add
    }

    // 好友消息撤回
    data class NoticeFriendRecall(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("message_id")
        val messageId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.friend_recall
    }

    // 群内戳一戳
    data class NoticeGroupPoke(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("message_id")
        val messageId: Long,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.poke
    }

    // 群红包运气王
    data class NoticeLuckyKing(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("target_id")
        val targetId: Long,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.notify

        enum class SubType {
            lucky_king,
        }
    }

    // 群成员荣誉变更
    data class NoticeGroupHonorChange(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("target_id")
        val targetId: Long,
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("honor_type")
        val honorType: HonorType,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.notice
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.notify

        enum class SubType {
            honor,
        }
        enum class HonorType {
            talkative, performer, emotion
        }
    }

    /************ 请求（request） ***********/

    enum class RequestType {
        friend,
        group,
    }

    // 加好友请求
    @Serializable
    data class RequestFriendAdd(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("comment")
        val comment: String,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.request
        @SerialName("request_type")
        val requestType: RequestType = RequestType.friend
    }

    // 加群请求/邀请
    @Serializable
    data class RequestGroupAdd(
        @SerialName("self_id")
        override val selfId: String,
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("comment")
        val comment: String,
        @SerialName("flag")
        val flag: String,
        @SerialName("id")
        override val id: String? = null,
        @SerialName("time")
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ): IOneBotV11Event {
        @SerialName("post_type")
        override val postType: PostType = PostType.request
        @SerialName("request_type")
        val requestType: RequestType = RequestType.group

        enum class SubType {
            add, invite
        }
    }
}