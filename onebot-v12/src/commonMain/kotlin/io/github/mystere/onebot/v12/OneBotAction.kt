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
        get_user_info(GetUserInfo::class),
        get_friend_list(GetFriendList::class),

        // 群动作
        get_group_info(GetGroupInfo::class),
        get_group_list(GetGroupList::class),
        get_group_member_info(GetGroupMemberInfo::class),
        get_group_member_list(GetGroupMemberList::class),
        set_group_name(SetGroupName::class),
        leave_group(LeaveGroup::class),

        // 群组动作
        get_guild_info(GetGuildInfo::class),
        get_guild_list(GetGuildList::class),
        set_guild_name(SetGuildName::class),
        get_guild_member_info(GetGuildMemberInfo::class),
        get_guild_member_list(GetGuildMemberList::class),
        leave_guild(LeaveGuild::class),
        get_channel_info(GetChannelInfo::class),
        get_channel_list(GetChannelList::class),
        set_channel_name(SetChannelName::class),
        get_channel_member_info(GetChannelMemberInfo::class),
        get_channel_member_list(GetChannelMemberList::class),
        leave_channel(LeaveChannel::class),

        unknown(Unknown::class);
    }

    @Serializable
    data class OriginEvent(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: OneBotV12Event.Type,
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

    @Serializable
    data class GetUserInfo(
        @SerialName("user_id")
        val userId: String,
    ): Param

    @Serializable
    data object GetFriendList: Param

    @Serializable
    data class GetGroupInfo(
        @SerialName("group_id")
        val groupId: String
    ): Param

    @Serializable
    data object GetGroupList: Param

    @Serializable
    data class GetGroupMemberInfo(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String
    ): Param

    @Serializable
    data class GetGroupMemberList(
        @SerialName("group_id")
        val groupId: String
    ): Param

    @Serializable
    data class SetGroupName(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("group_name")
        val groupName: String
    ): Param

    @Serializable
    data class LeaveGroup(
        @SerialName("group_id")
        val groupId: String,
    ): Param

    @Serializable
    data class GetGuildInfo(
        @SerialName("guild_id")
        val guildId: String
    ): Param

    @Serializable
    data object GetGuildList: Param

    @Serializable
    data class SetGuildName(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("guild_name")
        val guildName: String
    ): Param

    @Serializable
    data class GetGuildMemberInfo(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("user_id")
        val userId: String
    ): Param

    @Serializable
    data class GetGuildMemberList(
        @SerialName("guild_id")
        val guildId: String
    ): Param

    @Serializable
    data class LeaveGuild(
        @SerialName("guild_id")
        val guildId: String
    ): Param

    @Serializable
    data class GetChannelInfo(
        @SerialName("channel_id")
        val channelId: String
    ): Param

    @Serializable
    data object GetChannelList: Param

    @Serializable
    data class SetChannelName(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("channel_name")
        val channelName: String
    ): Param

    @Serializable
    data class GetChannelMemberInfo(
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("user_id")
        val userId: String
    ): Param

    @Serializable
    data class GetChannelMemberList(
        @SerialName("channel_id")
        val channelId: String
    ): Param

    @Serializable
    data class LeaveChannel(
        @SerialName("channel_id")
        val channelId: String
    ): Param
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
