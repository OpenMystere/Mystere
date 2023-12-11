package io.github.mystere.onebot.v12

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = IOneBotV12EventSerializer::class)
data class OneBotV12Event(
    val id: String? = null,
    val type: Type,
    val detailType: Enum<*>,
    val subType: Enum<*>? = null,
    val selfId: String,
    val time: Long = Clock.System.now().toEpochMilliseconds(),
    val params: Data,
): IOneBotEvent {
    constructor(
        id: String? = null,
        type: Type,
        detailType: Enum<*>,
        subType: Enum<*>? = null,
        selfId: String,
        time: Long,
        params: JsonObject,
    ): this(
        id, type, detailType,
        subType, selfId, time,
        CustomEvent(params)
    )

    @Serializable
    sealed interface Data: IOneBotEvent.Data
    
    @Serializable(with = CustomEventSerializer::class)
    data class CustomEvent(
        val data: JsonObject
    ): Data

    enum class Type {
        message, notice, request, meta;
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
        @SerialName("sub_type")
        val subType: MessageSubType,
        @SerialName("message_id")
        val messageId: Int,
        @SerialName("message")
        val message: CQCodeV12Message,
        @SerialName("raw_message")
        val rawIMessage: String,
        @SerialName("font")
        val font: Int,
        @SerialName("sender")
        val sender: Sender,
        @SerialName("user_id")
        val userId: Long = sender.userId,
    ): Data {
        @SerialName("message_type")
        val messageType: MessageType = MessageType.private

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
        @SerialName("message_type")
        val messageType: MessageType = MessageType.group,
        @SerialName("sub_type")
        val subType: MessageSubType,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("message")
        val message: CQCodeV12Message,
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
    ): Data {
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
    data class MetaConnect(
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
        @SerialName("group_id")
        val groupId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("file")
        val file: FileMeta,
    ): Data {
        @SerialName("notice_type")
        val noticeType: NoticeType = NoticeType.group_upload
    }
}

inline fun <reified T: @Serializable IOneBotEvent.Data> OneBotV12Event(
    id: String? = null,
    type: OneBotV12Event.Type,
    detailType: Enum<*>,
    subType: Enum<*>? = null,
    selfId: String,
    time: Long = Clock.System.now().toEpochMilliseconds(),
    params: T,
) = OneBotV12Event(
    id, type, detailType, subType, selfId, time, OneBotV12Event.CustomEvent(
        MystereJson.encodeToJsonElement(params).jsonObject
    ),
)
fun <T: IOneBotEvent.Data> OneBotV12Event(
    id: String? = null,
    type: OneBotV12Event.Type,
    detailType: Enum<*>,
    subType: Enum<*>? = null,
    selfId: String,
    time: Long = Clock.System.now().toEpochMilliseconds(),
    params: T,
    serializer: KSerializer<T>,
) = OneBotV12Event(
    id, type, detailType, subType, selfId, time, OneBotV12Event.CustomEvent(
        MystereJson.encodeToJsonElement(serializer, params).jsonObject
    ),
)

object IOneBotV12EventSerializer: KSerializer<OneBotV12Event> {
    override val descriptor: SerialDescriptor = serialDescriptor<OneBotV12Event>()
    override fun serialize(encoder: Encoder, value: OneBotV12Event) {
        (encoder as JsonEncoder).encodeJsonElement(buildJsonObject {
            put("self_id", value.selfId)
            put("id", value.id)
            put("time", value.time)
            put("type", value.type.name)
            put("detail_type", value.detailType.name)
            put("sub_type", value.subType?.name ?: "")
            for ((key, param) in MystereJson.encodeToJsonElement(value.params).jsonObject) {
                if (key == MystereJsonClassDiscriminator) {
                    continue
                }
                put(key, param)
            }
        })
    }

    override fun deserialize(decoder: Decoder): OneBotV12Event {
        throw NotImplementedError("Unnecessary implementation")
    }
}

object CustomEventSerializer: KSerializer<OneBotV12Event.CustomEvent> {
    override val descriptor: SerialDescriptor = serialDescriptor<OneBotV12Event.CustomEvent>()
    override fun serialize(encoder: Encoder, value: OneBotV12Event.CustomEvent) {
        (encoder as JsonEncoder).encodeJsonElement(value.data)
    }
    override fun deserialize(decoder: Decoder): OneBotV12Event.CustomEvent {
        throw NotImplementedError("Unnecessary implementation")
    }
}
