package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

@Serializable(with = OneBotV12ActionSerializer::class)
data class OneBotV12Action(
    val params: Param,
    val rawAction: String,
    val echo: JsonElement? = null,
): IOneBotAction {
    override val action: Action by lazy {
        try {
            Action.valueOf(rawAction)
        } catch (e: Exception) {
            Action.unknown
        }
    }

    @Serializable
    sealed interface Param: IOneBotAction.Param
    @Serializable
    data class Unknown(
        val content: JsonElement?
    ): Param
    enum class Action(
        val classDiscriminator: KClass<out Param>
    ): IOneBotAction.Action {
        // 消息动作
        send_message(SendMessage::class),
        delete_message(DeleteMessage::class),

        // 用户动作
        get_self_info(GetSelfInfo::class),

        unknown(Unknown::class);
    }

    @Serializable
    data class OriginEvent(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: IOneBotV12Event.PostType,
        @SerialName("detail_type")
        val detailType: String = "",
        @SerialName("sub_type")
        val subType: String = "",
    )

    @Serializable
    data class SendMessage(
        // private, group, channel, ...
        @SerialName("detail_type")
        val detailType: String,
        @SerialName("user_id")
        val userId: String? = null,
        @SerialName("group_id")
        val groupId: String? = null,
        @SerialName("guild_id")
        val guildId: String? = null,
        @SerialName("channel_id")
        val channelId: String? = null,
        @SerialName("message")
        val message: CQCodeV12Message,
        @SerialName("origin_event")
        val originEvent: OriginEvent? = null,
    ): Param

    @Serializable
    data class DeleteMessage(
        @SerialName("message_id")
        val messageId: String,
    ): Param

    @Serializable
    data object GetSelfInfo: Param
}

object OneBotV12ActionSerializer: KSerializer<OneBotV12Action> {
    override val descriptor: SerialDescriptor = serialDescriptor<OneBotV12Action>()

    override fun deserialize(decoder: Decoder): OneBotV12Action {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject
        val rawAction = json["action"]!!.jsonPrimitive.content
        val echo = json["echo"]
        try {
            val action = OneBotV12Action.Action.valueOf(rawAction)
            return OneBotV12Action(
                params = MystereJson.decodeFromJsonElement(buildJsonObject {
                    put(MystereJsonClassDiscriminator, JsonPrimitive(action.classDiscriminator.qualifiedName!!))
                    json["params"]?.jsonObject?.let { params ->
                        for ((key, value) in params) {
                            put(key, value)
                        }
                    }
                }),
                rawAction = rawAction,
                echo = echo,
            )
        } catch (e: Exception) {
            return OneBotV12Action(
                params = OneBotV12Action.Unknown(json["params"]),
                rawAction = rawAction,
                echo = echo,
            )
        }
    }

    override fun serialize(encoder: Encoder, value: OneBotV12Action) {
        encoder as JsonEncoder
        encoder.encodeJsonElement(buildJsonObject {
            for ((key, value) in MystereJson.encodeToJsonElement(value).jsonObject) {
                if (key == MystereJsonClassDiscriminator) {
                    continue
                }
                put(key, value)
            }
        })
    }
}

inline fun <reified T: IOneBotAction.Param> OneBotV12Action.castCustom(crossinline block: T.() -> Unit) {
    if (params !is OneBotV12Action.Unknown) {
        throw IllegalStateException("You should only call OneBotV12Action.castCustom() when custom action!")
    }
    MystereJson.decodeFromJsonElement<T>(params.content ?: JsonNull).run(block)
}
