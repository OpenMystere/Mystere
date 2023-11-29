package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class OneBotV11ActionResp(
    @SerialName("status")
    val status: IOneBotActionResp.Status,
    @SerialName("retcode")
    val retcode: RetCode,
    @SerialName("message")
    val message: String = "success.",
    @SerialName("data")
    val data: Data? = null,
    @SerialName("echo")
    val echo: JsonElement? = null,
): IOneBotActionResp {
    @Serializable(with = RetCodeSerializer::class)
    sealed class RetCode(
        override val rawCode: Int,
    ): IOneBotActionResp.RetCode {
        data object BadRequest: RetCode(1400)
        data object Unauthorized: RetCode(1401)
        data object Forbidden: RetCode(1403)
        data object NotFound: RetCode(1404)
        data object NotAcceptable: RetCode(1406)
        data object OK: RetCode(0)
        data class Custom(override val rawCode: Int): RetCode(rawCode)
    }
    @Serializable
    sealed interface Data: IOneBotActionResp.Data


    /**
     * @see OneBotV11Action.SendPrivateMsg
     * @see OneBotV11Action.SendGroupMsg
     * @see OneBotV11Action.SendMsg
     * @see OneBotV11Action.SendGuildChannelMsg
     */
    @Serializable
    data class MessageIdResp(
        @SerialName("message_id")
        val messageId: String,
    ): Data

    /**
     *  @see OneBotV11Action.GetMsg
     */
    @Serializable
    data class GetMessageResp(
        @SerialName("time")
        val time: Long,
        @SerialName("message_type")
        val messageType: IOneBotV11Event.MessageType,
        @SerialName("message_id")
        val messageId: String,
        @SerialName("real_id")
        val realId: String,
        @SerialName("sender")
        val sender: IOneBotV11Event.Sender,
    ): Data

    /**
     * @see OneBotV11Action.GetForwardMsg
     */
    @Serializable
    data class GetForwardMsgResp(
        @SerialName("message")
        val message: CQCodeV11Message,
    ): Data

    /**
     * @see OneBotV11Action.GetStrangerInfo
     */
    @Serializable
    data class GetStrangerInfoResp(
        @SerialName("user_id")
        val userId: String,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("sex")
        val sex: IOneBotV11Event.Sex,
        @SerialName("age")
        val age: Int,
    ): Data

    /**
     * @see OneBotV11Action.GetFriendList
     */
    @Serializable(with = GetFriendListRespSerializer::class)
    class GetFriendListResp(
        delegate: ArrayList<FriendListItem>
    ): List<GetFriendListResp.FriendListItem> by delegate, Data {
        @Serializable
        data class FriendListItem(
            @SerialName("user_id")
            val userId: String,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("sex")
            val sex: IOneBotV11Event.Sex,
            @SerialName("age")
            val age: Int,
        )
    }

    /**
     * @see OneBotV11Action.GetGroupInfo
     */
    @Serializable
    data class GetGroupInfoResp(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("group_name")
        val groupName: String,
        @SerialName("member_count")
        val memberCount: Int,
        @SerialName("max_member_count")
        val maxMemberCount: Int,
    ): Data

    /**
     * @see OneBotV11Action.GetFriendList
     */
    @Serializable(with = GetGroupListRespSerializer::class)
    class GetGroupListResp(
        delegate: ArrayList<GetGroupInfoResp>
    ): List<GetGroupInfoResp> by delegate, Data

    /**
     * @see OneBotV11Action.GetGroupMemberInfo
     */
    @Serializable
    data class GetGroupMemberInfo(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("card")
        val card: String,
        @SerialName("sex")
        val sex: IOneBotV11Event.Sex,
        @SerialName("age")
        val age: Int,
        @SerialName("area")
        val area: String,
        @SerialName("join_time")
        val joinTime: Long,
        @SerialName("last_sent_time")
        val lastSendTime: Long,
        @SerialName("level")
        val level: String,
        @SerialName("role")
        val role: Role,
        @SerialName("unfriendly")
        val unfriendly: Boolean,
        @SerialName("title")
        val title: String,
        @SerialName("title_expire_time")
        val titleExpireTime: Long,
        @SerialName("card_changeable")
        val cardChangeable: Boolean,
    ): Data {
        enum class Role {
            owner, admin, member
        }
    }

    /**
     * @see OneBotV11Action.GetGroupMemberList
     */
    @Serializable(with = GetGroupMemberListRespSerializer::class)
    class GetGroupMemberListResp(
        delegate: ArrayList<GetGroupMemberInfo>
    ): List<GetGroupMemberInfo> by delegate, Data

    /**
     * @see OneBotV11Action.GetGroupHonorInfo
     */
    @Serializable
    data class GetGroupHonorInfoResp(
        @SerialName("group_id")
        val groupId: String,
        @SerialName("current_talkative")
        val currentTalkative: CurrentTalkative? = null,
        @SerialName("talkative_list")
        val talkativeList: List<ListItem>? = null,
        @SerialName("performer_list")
        val performerList: List<ListItem>? = null,
        @SerialName("legend_list")
        val legendList: List<ListItem>? = null,
        @SerialName("strong_newbie_list")
        val strongNewbieList: List<ListItem>? = null,
        @SerialName("emotion_list")
        val emotionList: List<ListItem>? = null,
    ): Data {
        @Serializable
        data class CurrentTalkative(
            @SerialName("user_id")
            val userId: String,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("avatar")
            val avatar: String,
            @SerialName("day_count")
            val dayCount: String,
        )
        @Serializable
        data class ListItem(
            @SerialName("user_id")
            val userId: String,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("avatar")
            val avatar: String,
            @SerialName("description")
            val description: String,
        )
    }

    /**
     * @see OneBotV11Action.GetCookie
     */
    @Serializable
    data class GetCookieResp(
        @SerialName("cookies")
        val cookies: String,
    ): Data

    /**
     * @see OneBotV11Action.GetCsrfToken
     */
    @Serializable
    data class GetCsrfTokenResp(
        @SerialName("token")
        val token: Int,
    ): Data

    /**
     * @see OneBotV11Action.GetCredentials
     */
    @Serializable
    data class GetCredentialsResp(
        @SerialName("cookies")
        val cookies: String,
        @SerialName("token")
        val token: Int,
    ): Data

    /**
     * @see OneBotV11Action.GetRecord
     * @see OneBotV11Action.GetImage
     */
    @Serializable
    data class FileResp(
        @SerialName("file")
        val file: String,
    ): Data

    /**
     * @see OneBotV11Action.CanSendRecord
     * @see OneBotV11Action.CanSendImage
     */
    @Serializable
    data class CanSendResp(
        @SerialName("file")
        val file: String,
    ): Data

    /**
     * @see OneBotV11Action.GetGuildList
     */
    @Serializable(with = GuildInfoRespSerializer::class)
    class GetGuildListResp(
        delegate: ArrayList<GuildInfo>
    ): List<GetGuildListResp.GuildInfo> by delegate, Data {
        data class GuildInfo(
            @SerialName("guild_id")
            val guildId: String,
            @SerialName("guild_name")
            val guildName: String,
            @SerialName("guild_display_id")
            val guildDisplayId: String,
        )
    }

    /**
     * @see OneBotV11Action.GetGuildMetaByGuest
     */
    @Serializable
    data class GetGuildMetaByGuestResp(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("guild_name")
        val guildName: String,
        @SerialName("guild_profile")
        val guildProfile: String,
        @SerialName("create_time")
        val createTime: Long,
        @SerialName("max_member_count")
        val maxMemberCount: Long,
        @SerialName("max_robot_count")
        val maxRobotCount: Long,
        @SerialName("max_admin_count")
        val maxAdminCount: Long,
        @SerialName("member_count")
        val memberCount: Long,
        @SerialName("owner_id")
        val ownerId: String,
    ): Data

    /**
     * @see OneBotV11Action.GetGuildChannelList
     */
    @Serializable
    data class GetGuildChannelListResp(
        @SerialName("owner_guild_id")
        val ownerGuildId: String,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("channel_type")
        val channelType: Int,
        @SerialName("channel_name")
        val channelName: Long,
        @SerialName("create_time")
        val createTime: Long,
        @SerialName("creator_tiny_id")
        val creatorTinyId: String,
        @SerialName("talk_permission")
        val talkPermission: Int,
        @SerialName("visible_type")
        val visibleType: Int,
        @SerialName("current_slow_mode")
        val currentSlowMode: Int,
        @SerialName("slow_modes")
        val slowModes: List<SlowModeInfo>,
    ): Data {
        @Serializable
        data class SlowModeInfo(
            @SerialName("slow_mode_key")
            val slowModeKey: Int,
            @SerialName("slow_mode_text")
            val slowModeText: String,
            @SerialName("speak_frequency")
            val speakFrequency: Int,
            @SerialName("slow_mode_circle")
            val slowModeCircle: Int,
        )
    }

    /**
     * @see OneBotV11Action.GetGuildMemberList
     */
    @Serializable
    data class GetGuildMemberListResp(
        @SerialName("members")
        val members: List<GuildMemberInfo>,
        @SerialName("finished")
        val finished: Boolean,
        @SerialName("next_token")
        val nextToken: String,
    ): Data {
        @Serializable
        data class GuildMemberInfo(
            @SerialName("tiny_id")
            val tinyId: String,
            @SerialName("title")
            val title: String,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("role_id")
            val roleId: String,
            @SerialName("role_name")
            val roleName: String,
        )
    }

    /**
     * @see OneBotV11Action.GetGuildMemberProfile
     */
    @Serializable
    data class GetGuildMemberProfileResp(
        @SerialName("tiny_id")
        val tinyId: String,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("avatar_url")
        val avatarUrl: String,
        @SerialName("join_time")
        val joinTime: Long,
        @SerialName("roles")
        val roles: List<RoleInfo>,
    ): Data {
        @Serializable
        data class RoleInfo(
            @SerialName("role_id")
            val roleId: String,
            @SerialName("role_name")
            val roleName: String,
        )
    }

    /**
     * @see OneBotV11Action.GetTopicChannelFeeds
     */
    @Serializable(with = GetTopicChannelFeedsRespSerializer::class)
    class GetTopicChannelFeedsResp(
        delegate: ArrayList<FeedInfo>
    ): List<GetTopicChannelFeedsResp.FeedInfo> by delegate, Data {
        @Serializable
        data class FeedInfo(
            @SerialName("id")
            val id: String,
            @SerialName("channel_id")
            val channelId: String,
            @SerialName("guild_id")
            val guildId: String,
            @SerialName("create_time")
            val createTime: Long,
            @SerialName("title")
            val title: String,
            @SerialName("sub_title")
            val subTitle: String,
            @SerialName("poster_info")
            val posterInfo: List<PosterInfo>,
            @SerialName("resource")
            val resource: ResourceInfo,
        ) {
            @Serializable
            data class PosterInfo(
                @SerialName("tiny_id")
                val tinyId: String,
                @SerialName("nickname")
                val nickname: String,
                @SerialName("icon_url")
                val iconUrl: String,
            )
            @Serializable
            data class ResourceInfo(
                @SerialName("images")
                val images: List<FeedMedia>,
                @SerialName("videos")
                val videos: List<FeedMedia>,
            ) {
                @Serializable
                data class FeedMedia(
                    @SerialName("file_id")
                    val fileId: String,
                    @SerialName("pattern_id")
                    val patternId: String,
                    @SerialName("url")
                    val url: String,
                    @SerialName("height")
                    val height: Int,
                    @SerialName("width")
                    val width: Int,
                )
            }
            @Serializable
            data class FeedContent(
                @SerialName("type")
                val type: String,
                @SerialName("data")
                val data: JsonElement,
            )
        }
    }
}
private val codes: HashMap<Int, OneBotV11ActionResp.RetCode> = hashMapOf(
    0 to OneBotV11ActionResp.RetCode.OK,
    1400 to OneBotV11ActionResp.RetCode.BadRequest,
    1401 to OneBotV11ActionResp.RetCode.Unauthorized,
    1403 to OneBotV11ActionResp.RetCode.Forbidden,
    1404 to OneBotV11ActionResp.RetCode.NotFound,
    1406 to OneBotV11ActionResp.RetCode.NotAcceptable,
)
object RetCodeSerializer: KSerializer<OneBotV11ActionResp.RetCode> {
    override val descriptor: SerialDescriptor = serialDescriptor<Int>()

    override fun deserialize(decoder: Decoder): OneBotV11ActionResp.RetCode {
        return with(decoder.decodeInt()) {
            codes[this] ?: OneBotV11ActionResp.RetCode.Custom(this)
        }
    }

    override fun serialize(encoder: Encoder, value: OneBotV11ActionResp.RetCode) {
        encoder.encodeInt(value.rawCode)
    }
}

abstract class ListDelegateSerializer<ListT: List<ItemT>, ItemT: @Serializable Any>: KSerializer<ListT> {
    abstract override val descriptor: SerialDescriptor

    override fun deserialize(decoder: Decoder): ListT {
        val array = (decoder as JsonDecoder).decodeJsonElement().jsonArray
        val result: ArrayList<ItemT> = MystereJson.decodeFromJsonElement(array)
        return newList(result)
    }

    abstract fun newList(result: ArrayList<ItemT>): ListT

    override fun serialize(encoder: Encoder, value: ListT) {
        (encoder as JsonEncoder).encodeJsonElement(
            MystereJson.encodeToJsonElement(value as List<ItemT>)
        )
    }
}

object GetFriendListRespSerializer: ListDelegateSerializer<OneBotV11ActionResp.GetFriendListResp, OneBotV11ActionResp.GetFriendListResp.FriendListItem>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<OneBotV11ActionResp.GetFriendListResp.FriendListItem>()
    override fun newList(result: ArrayList<OneBotV11ActionResp.GetFriendListResp.FriendListItem>): OneBotV11ActionResp.GetFriendListResp {
        return OneBotV11ActionResp.GetFriendListResp(result)
    }
}

object GetGroupListRespSerializer: ListDelegateSerializer<OneBotV11ActionResp.GetGroupListResp, OneBotV11ActionResp.GetGroupInfoResp>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<OneBotV11ActionResp.GetGroupInfoResp>()
    override fun newList(result: ArrayList<OneBotV11ActionResp.GetGroupInfoResp>): OneBotV11ActionResp.GetGroupListResp {
        return OneBotV11ActionResp.GetGroupListResp(result)
    }
}

object GetGroupMemberListRespSerializer: ListDelegateSerializer<OneBotV11ActionResp.GetGroupMemberListResp, OneBotV11ActionResp.GetGroupMemberInfo>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<OneBotV11ActionResp.GetGroupMemberInfo>()
    override fun newList(result: ArrayList<OneBotV11ActionResp.GetGroupMemberInfo>): OneBotV11ActionResp.GetGroupMemberListResp {
        return OneBotV11ActionResp.GetGroupMemberListResp(result)
    }
}

object GuildInfoRespSerializer: ListDelegateSerializer<OneBotV11ActionResp.GetGuildListResp, OneBotV11ActionResp.GetGuildListResp.GuildInfo>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<OneBotV11ActionResp.GetGuildListResp.GuildInfo>()
    override fun newList(result: ArrayList<OneBotV11ActionResp.GetGuildListResp.GuildInfo>): OneBotV11ActionResp.GetGuildListResp {
        return OneBotV11ActionResp.GetGuildListResp(result)
    }
}

object GetTopicChannelFeedsRespSerializer: ListDelegateSerializer<OneBotV11ActionResp.GetTopicChannelFeedsResp, OneBotV11ActionResp.GetTopicChannelFeedsResp.FeedInfo>() {
    override val descriptor: SerialDescriptor = listSerialDescriptor<OneBotV11ActionResp.GetTopicChannelFeedsResp.FeedInfo>()
    override fun newList(result: ArrayList<OneBotV11ActionResp.GetTopicChannelFeedsResp.FeedInfo>): OneBotV11ActionResp.GetTopicChannelFeedsResp {
        return OneBotV11ActionResp.GetTopicChannelFeedsResp(result)
    }
}
