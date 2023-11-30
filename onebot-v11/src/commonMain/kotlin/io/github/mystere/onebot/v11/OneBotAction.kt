package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

@Serializable(with = OneBotV11ActionSerializer::class)
data class OneBotV11Action(
    val params: Param,
    val rawAction: String,
    val echo: JsonElement? = null,
): IOneBotAction {
    override val action: Action by lazy {
        try {
            Action.valueOf(rawAction.replace("_async", "")
                .replace("_rate_limited", ""))
        } catch (e: Exception) {
            Action.unknown
        }
    }
    val async: Boolean by lazy { rawAction.contains("_async") }
    val rateLimited: Boolean by lazy { rawAction.contains("_rate_limited") }

    @Serializable
    sealed interface Param: IOneBotAction.Param
    @Serializable
    data class Unknown(
        val content: JsonElement?
    ): Param
    enum class Action(
        val classDiscriminator: KClass<out Param>
    ): IOneBotAction.Action {
        send_private_msg(SendPrivateMsg::class),
        send_group_msg(SendGroupMsg::class),
        send_msg(SendMsg::class),
        delete_msg(DeleteMsg::class),
        get_msg(GetMsg::class),
        get_forward_msg(GetForwardMsg::class),
        send_like(SendLike::class),
        set_group_kick(SetGroupKick::class),
        set_group_ban(SetGroupBan::class),
        set_group_anonymous_ban(SetGroupAnonymousBan::class),
        set_group_whole_ban(SetGroupWholeBan::class),
        set_group_admin(SetGroupAdmin::class),
        set_group_anonymous(SetGroupAnonymous::class),
        set_group_card(SetGroupCard::class),
        set_group_name(SetGroupName::class),
        set_group_leave(SetGroupLeave::class),
        set_group_special_title(SetGroupSpecialTitle::class),
        set_friend_add_request(SetFriendAddRequest::class),
        set_group_add_request(SetGroupAddRequest::class),
        get_login_info(GetLoginInfo::class),
        get_stranger_info(GetStrangerInfo::class),
        get_friend_list(GetFriendList::class),
        get_group_info(GetGroupInfo::class),
        get_group_member_info(GetGroupMemberInfo::class),
        get_group_member_list(GetGroupMemberList::class),
        get_group_honor_info(GetGroupHonorInfo::class),
        get_cookies(GetCookie::class),
        get_csrf_token(GetCsrfToken::class),
        get_credentials(GetCredentials::class),
        get_record(GetRecord::class),
        get_image(GetImage::class),
        can_send_image(CanSendImage::class),
        can_send_record(CanSendRecord::class),
        get_status(GetStatus::class),
        get_version_info(GetVersionInfo::class),
        set_restart(SetRestart::class),
        clean_cache(CleanCache::class),

        get_guild_service_profile(GetGuildServiceProfile::class),
        get_guild_list(GetGuildList::class),
        get_guild_meta_by_guest(GetGuildMetaByGuest::class),
        get_guild_channel_list(GetGuildChannelList::class),
        get_guild_member_list(GetGuildMemberList::class),
        get_guild_member_profile(GetGuildMemberProfile::class),
        send_guild_channel_msg(SendGuildChannelMsg::class),
        get_topic_channel_feeds(GetTopicChannelFeeds::class),
        delete_guild_role(DeleteGuildRole::class),
        get_guild_msg(GetGuildMsg::class),
        get_guild_roles(GetGuildRoles::class),
        set_guild_member_role(SetGuildMemberRole::class),
        update_guild_role(UpdateGuildRole::class),
        create_guild_role(CreateGuildRole::class),

        unknown(Unknown::class);
    }

    @Serializable
    data class OriginEvent(
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: IOneBotV11Event.PostType,
        @SerialName("detail_type")
        val detailType: String = "",
        @SerialName("sub_type")
        val subType: String = "",
    )

    @Serializable
    data class SendPrivateMsg(
        @SerialName("user_id")
        val userId: String,
        @SerialName("message")
        val message: CQCodeV11Message,
        @SerialName("auto_escape")
        val autoEscape: Boolean = false,
        @SerialName("origin_event")
        val originEvent: OriginEvent? = null,
    ): Param

    @Serializable
    data class SendGroupMsg(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("message")
        val message: CQCodeV11Message,
        @SerialName("auto_escape")
        val autoEscape: Boolean = false,
        @SerialName("origin_event")
        val originEvent: OriginEvent? = null,
    ): Param

    @Serializable
    data class SendMsg(
        @SerialName("message_type")
        val messageType: MessageType? = null,
        @SerialName("user_id")
        val userId: String? = null,
        @SerialName("group_id")
        val groupId: String? = null,
        @SerialName("message")
        val message: CQCodeV11Message,
        @SerialName("auto_escape")
        val autoEscape: Boolean = false,
        @SerialName("origin_event")
        val originEvent: OriginEvent? = null,
    ): Param {
        enum class MessageType {
            private, group
        }
    }

    @Serializable
    data class DeleteMsg(
        @SerialName("message_id")
        val messageId: String,
    ): Param

    @Serializable
    data class GetMsg(
        @SerialName("message_id")
        val messageId: String,
    ): Param

    @Serializable
    data class GetForwardMsg(
        @SerialName("id")
        val id: String,
    ): Param

    @Serializable
    data class SendLike(
        @SerialName("user_id")
        val userId: String,
        @SerialName("times")
        val times: Int = 1,
    ): Param

    @Serializable
    data class SetGroupKick(
        @SerialName("user_id")
        val userId: String,
        @SerialName("times")
        val times: Int = 1,
    ): Param

    @Serializable
    data class SetGroupBan(
        @SerialName("user_id")
        val userId: String,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("duration")
        val duration: Long = 30 * 60,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupAnonymousBan(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("anonymous")
        val anonymous: CQCodeV11MessageItem.Anonymous,
        @SerialName("flag")
        @JsonNames("anonymous_flag")
        val flag: String,
        @SerialName("duration")
        val duration: Long = 30 * 60,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupWholeBan(
        @SerialName("group_id")
        val groupId: String,
        @JsonNames("enable")
        val enable: Boolean,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupAdmin(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @JsonNames("enable")
        val enable: Boolean,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupAnonymous(
        @SerialName("group_id")
        val groupId: String,
        @JsonNames("enable")
        val enable: Boolean,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupCard(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @JsonNames("card")
        val card: String? = null,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupName(
        @SerialName("group_id")
        val groupId: String,
        @JsonNames("group_name")
        val groupName: String,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupLeave(
        @SerialName("group_id")
        val groupId: String,
        @JsonNames("is_dismiss")
        val isDismiss: Boolean = false,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupSpecialTitle(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @JsonNames("special_title")
        val specialTitle: String? = null,
        @JsonNames("duration")
        val duration: Int = -1,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetFriendAddRequest(
        @SerialName("flag")
        val flag: String,
        @SerialName("approve")
        val approve: Boolean = true,
        @JsonNames("remark")
        val remark: String? = null,
    ): Param

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class SetGroupAddRequest(
        @SerialName("flag")
        val flag: String,
        @SerialName("approve")
        val approve: Boolean = true,
        @SerialName("type")
        @JsonNames("sub_type")
        val type: IOneBotV11Event.RequestGroupAdd.SubType? = null,
        @SerialName("reason")
        val reason: IOneBotV11Event.RequestGroupAdd.SubType? = null,
    ): Param

    @Serializable
    data object GetLoginInfo: Param

    @Serializable
    data class GetStrangerInfo(
        @SerialName("user_id")
        val userId: String,
        @SerialName("no_cache")
        val noCache: Boolean = false,
    ): Param

    @Serializable
    data object GetFriendList: Param

    @Serializable
    data class GetGroupInfo(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("no_cache")
        val noCache: Boolean = false,
    ): Param

    @Serializable
    data object GetGroupList: Param

    @Serializable
    data class GetGroupMemberInfo(
        @SerialName("user_id")
        val userId: String,
        @SerialName("group_id")
        val groupId: String,
        @SerialName("no_cache")
        val noCache: Boolean = false,
    ): Param

    @Serializable
    data class GetGroupMemberList(
        @SerialName("group_id")
        val groupId: String,
    ): Param

    @Serializable
    data class GetGroupHonorInfo(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("type")
        val type: Type,
    ): Param {
        enum class Type {
            talkative, performer, legend, strong_newbie, emotion, all
        }
    }

    @Serializable
    data class GetCookie(
        @SerialName("domain")
        val domain: String,
    ): Param

    @Serializable
    data object GetCsrfToken: Param

    @Serializable
    data class GetCredentials(
        @SerialName("domain")
        val domain: String,
    ): Param

    @Serializable
    data class GetRecord(
        @SerialName("file")
        val file: String,
        @SerialName("out_format")
        val outFormat: String,
    ): Param

    @Serializable
    data class GetImage(
        @SerialName("file")
        val file: String,
    ): Param

    @Serializable
    data object CanSendImage: Param

    @Serializable
    data object CanSendRecord: Param

    @Serializable
    data object GetStatus: Param

    @Serializable
    data object GetVersionInfo: Param

    @Serializable
    data class SetRestart(
        @SerialName("delay")
        val delay: Long,
    ): Param

    @Serializable
    data object CleanCache: Param




    @Serializable
    data object GetGuildServiceProfile: Param

    @Serializable
    data object GetGuildList: Param

    @Serializable
    data class GetGuildMetaByGuest(
        @SerialName("guild_id")
        val guildId: String,
    ): Param

    @Serializable
    data class GetGuildChannelList(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("no_cache")
        val noCache: Boolean = false,
    ): Param

    @Serializable
    data class GetGuildMemberList(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("next_token")
        val nextToken: String,
    ): Param

    @Serializable
    data class GetGuildMemberProfile(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("user_id")
        val userId: String,
    ): Param

    @Serializable
    data class SendGuildChannelMsg(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("message")
        val message: CQCodeV11Message,
        @SerialName("origin_event")
        val originEvent: OriginEvent? = null,
    ): Param

    @Serializable
    data class GetTopicChannelFeeds(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String,
    ): Param

    @Serializable
    data class DeleteGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("role_id")
        val roleId: String,
    ): Param

    @Serializable
    data class GetGuildMsg(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("no_cache")
        val noCache: Boolean = false,
    ): Param

    @Serializable
    data class GetGuildRoles(
        @SerialName("guild_id")
        val guildId: String,
    ): Param

    @Serializable
    data class SetGuildMemberRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("set")
        val set: Boolean,
        @SerialName("role_id")
        val roleId: String,
    ): Param

    @Serializable
    data class UpdateGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("role_id")
        val roleId: String,
        @SerialName("name")
        val name: String,
        @SerialName("color")
        val color: String,
        @SerialName("independent")
        val independent: Boolean = false,
    ): Param

    @Serializable
    data class CreateGuildRole(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("name")
        val name: String,
        @SerialName("color")
        val color: String,
        @SerialName("independent")
        val independent: Boolean = false,
        @SerialName("initial_users")
        val initialUsers: List<String>,
    ): Param
}

//suspend inline fun <reified T: OneBotV11Action.Param> OneBotV11Action.withParams(crossinline block: suspend T.() -> Unit) {
//    block.invoke(MystereJson.decodeFromJsonElement(params))
//}

object OneBotV11ActionSerializer: KSerializer<OneBotV11Action> {
    override val descriptor: SerialDescriptor = serialDescriptor<OneBotV11Action>()

    override fun deserialize(decoder: Decoder): OneBotV11Action {
        decoder as JsonDecoder
        val json = decoder.decodeJsonElement().jsonObject
        val rawAction = json["action"]!!.jsonPrimitive.content
        val echo = json["echo"]
        try {
            val action = OneBotV11Action.Action.valueOf(rawAction)
            return OneBotV11Action(
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
            return OneBotV11Action(
                params = OneBotV11Action.Unknown(json["params"]),
                rawAction = rawAction,
                echo = echo,
            )
        }
    }

    override fun serialize(encoder: Encoder, value: OneBotV11Action) {
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

inline fun <reified T: IOneBotAction.Param> OneBotV11Action.castCustom(crossinline block: T.() -> Unit) {
    if (params !is OneBotV11Action.Unknown) {
        throw IllegalStateException("You should only call OneBotV11Action.castCustom() when custom action!")
    }
    MystereJson.decodeFromJsonElement<T>(params.content ?: JsonNull).run(block)
}
