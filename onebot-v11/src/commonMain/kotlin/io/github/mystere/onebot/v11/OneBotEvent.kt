package io.github.mystere.onebot.v11

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import kotlinx.datetime.Clock
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = OneBotV11EventSerializer::class)
data class OneBotV11Event(
    val id: String? = null,
    val postType: PostType,
    val selfId: String,
    val time: Long = Clock.System.now().toEpochMilliseconds(),
    val params: Data,
): IOneBotEvent {
    constructor(
        id: String?,
        postType: PostType,
        selfId: String,
        time: Long,
        params: JsonObject,
    ): this(
        id, postType, selfId,
        time, CustomEvent(params)
    )

    @Serializable
    sealed interface Data: IOneBotEvent.Data

    @Serializable(with = CustomEventSerializer::class)
    data class CustomEvent(
        val data: JsonObject
    ): Data

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
        val nickname: String = "用户",
        @SerialName("sex")
        val sex: Sex = Sex.unknown,
        @SerialName("age")
        val age: Int? = null,
    )

    // 私聊消息
    @Serializable
    data class MessagePrivate(
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
    ): Data {
        @SerialName("message_type")
        val messageType: MessageType = MessageType.private
    }

    // 消息
    @Serializable
    data class Message(
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
        @SerialName("memberOpenid")
        val groupId: String? = null,
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("self_tiny_id")
        val selfTinyId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        @SerialName("anonymous")
        val anonymous: Anonymous? = null,
    ): Data {
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
        @SerialName("sub_type")
        val subType: LifecycleSubType,
    ): Data {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.lifecycle
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
        @SerialName("state")
        val state: HeartbeatStatus,
    ): Data {
        @SerialName("meta_event_type")
        val metaEventType: MetaEventType = MetaEventType.heartbeat
    }


    /************ 通知事件（notice） ***********/

    enum class NoticeType {
        group_upload,
        group_admin,
        group_decrease, group_increase,
        group_ban,
        group_recall,
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
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("file")
        val file: FileMeta,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_upload
    }

    // 群管理员变动
    @Serializable
    data class NoticeGroupAdminChange(
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_admin

        enum class SubType {
            set, unset
        }
    }

    // 群成员减少
    @Serializable
    data class NoticeGroupMemberDecrease(
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("operator_id")
        val operatorId: Long,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_decrease

        enum class SubType {
            leave, kick, kick_me
        }
    }

    // 群成员增加
    @Serializable
    data class NoticeGroupMemberIncrease(
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("operator_id")
        val operatorId: Long,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_increase

        enum class SubType {
            leave, kick, kick_me
        }
    }

    // 群禁言
    @Serializable
    data class NoticeGroupBan(
        @SerialName("sub_type")
        val subType: SubType,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("operator_id")
        val operatorId: Long,
        @SerialName("duration")
        val duration: Long,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_ban

        enum class SubType {
            ban, lift_ban
        }
    }

    // 群禁言
    @Serializable
    data class NoticeFriendAdd(
        @SerialName("user_id")
        val userId: String,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.friend_add
    }

    // 群禁言
    @Serializable
    data class NoticeGroupRecall(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_recall
    }

    // 好友消息撤回
    @Serializable
    data class NoticeFriendRecall(
        @SerialName("user_id")
        val userId: Long,
        @SerialName("message_id")
        val messageId: Long,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.friend_recall
    }

    // 群内戳一戳
    @Serializable
    data class NoticeGroupPoke(
        @SerialName("user_id")
        val userId: Long,
        @SerialName("message_id")
        val messageId: Long,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.poke
    }

    // 群红包运气王
    @Serializable
    data class NoticeLuckyKing(
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("target_id")
        val targetId: Long,
        @SerialName("sub_type")
        val subType: SubType,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.notify

        enum class SubType {
            lucky_king,
        }
    }

    // 群成员荣誉变更
    @Serializable
    data class NoticeGroupHonorChange(
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
    ): Data {
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
        @SerialName("user_id")
        val userId: Long,
        @SerialName("comment")
        val comment: String,
    ): Data {
        @SerialName("request_type")
        val requestType: RequestType = RequestType.friend
    }

    // 加群请求/邀请
    @Serializable
    data class RequestGroupAdd(
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("comment")
        val comment: String,
        @SerialName("flag")
        val flag: String,
    ): Data {
        @SerialName("request_type")
        val requestType: RequestType = RequestType.group

        enum class SubType {
            add, invite
        }
    }
}

inline fun <reified T: @Serializable IOneBotEvent.Data> OneBotV11Event(
    id: String? = null,
    postType: OneBotV11Event.PostType,
    selfId: String,

    time: Long = Clock.System.now().toEpochMilliseconds(),

    params: T,
) = OneBotV11Event(
    id, postType, selfId, time,
    OneBotV11Event.CustomEvent(
        MystereJson.encodeToJsonElement(params).jsonObject
    ),
)
fun <T: IOneBotEvent.Data> OneBotV11Event(
    id: String? = null,
    postType: OneBotV11Event.PostType,
    selfId: String,

    time: Long = Clock.System.now().toEpochMilliseconds(),

    params: T,
    serializer: KSerializer<T>,
) = OneBotV11Event(
    id, postType, selfId, time, OneBotV11Event.CustomEvent(
        MystereJson.encodeToJsonElement(serializer, params).jsonObject
    ),
)

object OneBotV11EventSerializer: KSerializer<OneBotV11Event> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        serialName = "io.github.mystere.onebot.v11.BotV11Event",
        kind = StructureKind.OBJECT
    )

    override fun serialize(encoder: Encoder, value: OneBotV11Event) {
        (encoder as JsonEncoder).encodeJsonElement(buildJsonObject {
            put("self_id", value.selfId)
            put("id", value.id)
            put("time", value.time)
            put("post_type", value.postType.name)
            for ((key, param) in MystereJson.encodeToJsonElement(value.params).jsonObject) {
                if (key == MystereJsonClassDiscriminator) {
                    continue
                }
                put(key, param)
            }
        })
    }

    override fun deserialize(decoder: Decoder): OneBotV11Event {
        throw NotImplementedError("Unnecessary implementation")
    }
}

object CustomEventSerializer: KSerializer<OneBotV11Event.CustomEvent> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        serialName = "io.github.mystere.onebot.v11.BotV11Event\$CustomEvent",
        kind = StructureKind.OBJECT
    )
    override fun serialize(encoder: Encoder, value: OneBotV11Event.CustomEvent) {
        (encoder as JsonEncoder).encodeJsonElement(value.data)
    }
    override fun deserialize(decoder: Decoder): OneBotV11Event.CustomEvent {
        throw NotImplementedError("Unnecessary implementation")
    }
}
