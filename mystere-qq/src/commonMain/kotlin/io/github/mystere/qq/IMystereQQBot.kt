package io.github.mystere.qq

import io.github.mystere.core.IMystereBotConnection
import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.*
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.database.IQQDatabase
import io.github.mystere.qq.v11.MystereV11QQBot
import io.github.mystere.qq.v12.MystereV12QQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.http.IQQBotAPI
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

abstract class IMystereQQBot<ActionT: IOneBotAction, EventT: IOneBotEvent, RespT: IOneBotActionResp> protected constructor(
    protected val config: QQBot.Config,
    connection: IOneBotConnection<ActionT, EventT, RespT>,
): IOneBot<EventT, ActionT, RespT>(connection) {
    protected abstract val QQDatabase: IQQDatabase

    override val botId: String get() = botUser.id

    private var _botUser: OpCode0.Ready.User? = null
    protected val botUser: OpCode0.Ready.User get() = _botUser!!
    private val mQQBot: QQBot by lazy { QQBot.create(config) }
    protected val QQBotApi: IQQBotAPI get() = mQQBot.BotAPI
    final override suspend fun connect() {
        mQQBot.connect()
        val botUserResult = CompletableDeferred<OpCode0.Ready.User>()
        coroutineScope.launch(Dispatchers.IO) {
            val childScope = CoroutineScope(coroutineScope.coroutineContext + Job())
            for (payload: QQBotWebsocketPayload in mQQBot) {
                childScope.launch(Dispatchers.IO) {
                    try {
                        if (payload.opCode == QQBotWebsocketPayload.OpCode.Dispatch && payload.type == "READY") {
                            if (_botUser == null) {
                                payload.withData<OpCode0.Ready> {
                                    botUserResult.complete(user)
                                }
                            }
                        } else {
                            processQQEvent(payload)
                        }
                    } catch (e: Throwable) {
                        log.warn(e) { "process qq event error" }
                    }
                }
            }
        }
        try {
            withTimeout(30_000) {
                _botUser = botUserResult.await()
                connect(botUser.id)
            }
        } catch (e: TimeoutCancellationException) {
            log.error(e) { "Connect to QQ OpenAPI timeout!" }
            disconnect()
        }
    }

    private suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (val originType = event.type) {
                // 频道全量消息
                "MESSAGE_CREATE",
                // 频道@消息
                "AT_MESSAGE_CREATE",
                // 频道私聊消息
                "DIRECT_MESSAGE_CREATE", -> event.withData<OpCode0.GuildMessage> {
                    processGuildMessageEvent(originType, this)
                }
                // 群@消息
                "GROUP_AT_MESSAGE_CREATE" -> event.withData<OpCode0.GroupMessage> {
                    processGroupMessageEvent(originType, this)
                }
                // 单聊消息
                "C2C_MESSAGE_CREATE" -> event.withData<OpCode0.C2CMessage> {
                    processC2CMessageEvent(originType, this)
                }
                // 频道创建
                "GUILD_CREATE" -> event.withData<OpCode0.GuildInfo> {
                    processGuildEvent(GuildEventType.create, this)
                }
                // 频道更新
                "GUILD_UPDATE" -> event.withData<OpCode0.GuildInfo> {
                    processGuildEvent(GuildEventType.update, this)
                }
                // 频道删除
                "GUILD_DELETE" -> event.withData<OpCode0.GuildInfo> {
                    processGuildEvent(GuildEventType.create, this)
                }
                // 子频道创建
                "CHANNEL_CREATE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelEvent(ChannelEventType.create, this)
                }
                // 子频道更新
                "CHANNEL_UPDATE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelEvent(ChannelEventType.update, this)
                }
                // 子频道删除
                "CHANNEL_DELETE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelEvent(ChannelEventType.delete, this)
                }
                // 频道用户新增
                "GUILD_MEMBER_ADD" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberEvent(GuildMemberEventType.add, this)
                }
                // 频道用户更新
                "GUILD_MEMBER_UPDATE" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberEvent(GuildMemberEventType.update, this)
                }
                // 频道用户离开
                "GUILD_MEMBER_REMOVE" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberEvent(GuildMemberEventType.remove, this)
                }
                // 用户进入音视频/直播子频道时
                "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER" -> event.withData<OpCode0.AudioLiveChannelMember> {
                    processAudioLiveChannelEvent(AudioLiveChannelEventType.enter, this)
                }
                // 用户离开音视频/直播子频道时
                "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT" -> event.withData<OpCode0.AudioLiveChannelMember> {
                    processAudioLiveChannelEvent(AudioLiveChannelEventType.exit, this)
                }
                // 机器人加入群聊
                "GROUP_ADD_ROBOT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupRobotEvent(GroupRobotEventType.add, this)
                }
                // 机器人退出群聊
                "GROUP_DEL_ROBOT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupRobotEvent(GroupRobotEventType.del, this)
                }
                // 群聊拒绝机器人主动消息
                "GROUP_MSG_REJECT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupRobotEvent(GroupRobotEventType.reject, this)
                }
                // 群聊接受机器人主动消息
                "GROUP_MSG_RECEIVE" -> event.withData<OpCode0.GroupRobot> {
                    processGroupRobotEvent(GroupRobotEventType.receive, this)
                }
                // 用户添加机器人
                "FRIEND_ADD" -> event.withData<OpCode0.UserRobot> {
                    processUserRobotEvent(UserRobotEventType.add, this)
                }
                // 用户删除机器人
                "FRIEND_DEL" -> event.withData<OpCode0.UserRobot> {
                    processUserRobotEvent(UserRobotEventType.del, this)
                }
                // 用户拒绝机器人主动消息
                "C2C_MSG_REJECT" -> event.withData<OpCode0.UserRobot> {
                    processUserRobotEvent(UserRobotEventType.reject, this)
                }
                // 用户允许机器人主动消息
                "C2C_MSG_RECEIVE" -> event.withData<OpCode0.UserRobot> {
                    processUserRobotEvent(UserRobotEventType.receive, this)
                }
                // 用户对消息进行表情表态时
                "MESSAGE_REACTION_ADD" -> event.withData<OpCode0.MessageReaction> {
                    processMessageReactionEvent(MessageReactionEventType.add, this)
                }
                // 用户对消息进行取消表情表态时
                "MESSAGE_REACTION_REMOVE" -> event.withData<OpCode0.MessageReaction> {
                    processMessageReactionEvent(MessageReactionEventType.remove, this)
                }
            }
            else -> { }
        }
    }

    protected abstract suspend fun processGuildMessageEvent(originType: String, message: OpCode0.GuildMessage)
    protected abstract suspend fun processGroupMessageEvent(originType: String, message: OpCode0.GroupMessage)
    protected abstract suspend fun processC2CMessageEvent(originType: String, message: OpCode0.C2CMessage)
    protected abstract suspend fun processGuildEvent(eventType: GuildEventType, message: OpCode0.GuildInfo)
    protected abstract suspend fun processChannelEvent(eventType: ChannelEventType, message: OpCode0.ChannelInfo)
    protected abstract suspend fun processGuildMemberEvent(eventType: GuildMemberEventType, message: OpCode0.GuildMember)
    protected abstract suspend fun processAudioLiveChannelEvent(eventType: AudioLiveChannelEventType, message: OpCode0.AudioLiveChannelMember)
    protected abstract suspend fun processGroupRobotEvent(eventType: GroupRobotEventType, message: OpCode0.GroupRobot)
    protected abstract suspend fun processUserRobotEvent(eventType: UserRobotEventType, message: OpCode0.UserRobot)
    protected abstract suspend fun processMessageReactionEvent(eventType: MessageReactionEventType, message: OpCode0.MessageReaction)

    protected suspend fun processOneBotQQAction(action: OneBotQQAction, params: JsonElement?, echo: JsonElement?): RespT {
        try {
            val respData: OneBotQQActionRespData? = when (action) {
                OneBotQQAction.rich_media_group -> params.castQQCustom<OneBotQQActionParam.RichMedia> {
                    processRichMediaGroupAction(this)
                }
                OneBotQQAction.rich_media_c2c -> params.castQQCustom<OneBotQQActionParam.RichMedia> {
                    processRichMediaC2CAction(this)
                }
                OneBotQQAction.message_reaction_put -> params.castQQCustom<OneBotQQActionParam.MessageReaction> {
                    processMessageReactionPutAction(this)
                }
                OneBotQQAction.message_reaction_delete -> params.castQQCustom<OneBotQQActionParam.MessageReaction> {
                    processMessageReactionDeleteAction(this)
                }
                OneBotQQAction.message_reaction_get -> params.castQQCustom<OneBotQQActionParam.MessageReaction> {
                    processMessageReactionGetAction(this)
                }
//                else -> throw OneBotNotImplementedException(action)
            }
            return createSuccessResp(
                data = respData?.let {
                    MystereJson.encodeToJsonElement(it)
                },
                echo = echo,
            )
        } catch (e: OneBotException) {
            return createFailedResp(e)
        }
    }

    private suspend fun processRichMediaGroupAction(params: OneBotQQActionParam.RichMedia): OneBotQQActionRespData? {
        TODO("Not implemented yet.")
    }
    private suspend fun processRichMediaC2CAction(params: OneBotQQActionParam.RichMedia): OneBotQQActionRespData? {
        TODO("Not implemented yet.")
    }
    private suspend fun processMessageReactionPutAction(params: OneBotQQActionParam.MessageReaction): OneBotQQActionRespData? {
        TODO("Not implemented yet.")
    }
    private suspend fun processMessageReactionDeleteAction(params: OneBotQQActionParam.MessageReaction): OneBotQQActionRespData? {
        TODO("Not implemented yet.")
    }
    private suspend fun processMessageReactionGetAction(params: OneBotQQActionParam.MessageReaction): OneBotQQActionRespData? {
        TODO("Not implemented yet.")
    }


    final override suspend fun disconnect() {
        mQQBot.disconnect()
        coroutineScope.cancel()
    }

    final override fun equals(other: Any?): Boolean {
        return if (other is IMystereQQBot<*, *, *>) other.botId == botId else false
    }

    final override fun hashCode(): Int {
        return botId.hashCode()
    }
    protected abstract fun createSuccessResp(
        data: JsonElement? = null,
        echo: JsonElement? = null,
    ): RespT
    protected abstract fun createFailedResp(e: OneBotException): RespT

    companion object {
        fun create(
            config: QQBot.Config,
            connection: IMystereBotConnection<*, *, *>,
        ): IMystereQQBot<*, *, *> {
            return when (connection) {
                is IOneBotV11Connection -> MystereV11QQBot(config, connection)
                is IOneBotV12Connection -> MystereV12QQBot(config, connection)
                else -> throw UnsupportedOperationException("Unsupported connection type: ${config::class}")
            }
        }
        fun create(
            config: QQBot.Config,
            connectionConfig: IMystereBotConnection.IConfig<*>,
        ): IMystereQQBot<*, *, *> {
            return create(config, connectionConfig.createConnection(config.appId))
        }
    }
}