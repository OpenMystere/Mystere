package io.github.mystere.qq

import io.github.mystere.core.IMystereBotConnection
import io.github.mystere.onebot.*
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.v11.MystereV11QQBot
import io.github.mystere.qq.v12.MystereV12QQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.http.IQQBotAPI
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import kotlinx.coroutines.*

abstract class IMystereQQBot<ActionT: IOneBotAction, EventT: IOneBotEvent, RespT: IOneBotActionResp> protected constructor(
    protected val config: QQBot.Config,
    connection: IOneBotConnection<ActionT, EventT, RespT>,
): IOneBot<EventT, ActionT, RespT>(connection) {
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
                    processGuildCreateEvent(originType, this)
                }
                // 频道更新
                "GUILD_UPDATE" -> event.withData<OpCode0.GuildInfo> {
                    processGuildUpdateEvent(originType, this)
                }
                // 频道删除
                "GUILD_DELETE" -> event.withData<OpCode0.GuildInfo> {
                    processGuildDeleteEvent(originType, this)
                }
                // 子频道创建
                "CHANNEL_CREATE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelCreateEvent(originType, this)
                }
                // 子频道更新
                "CHANNEL_UPDATE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelUpdateEvent(originType, this)
                }
                // 子频道删除
                "CHANNEL_DELETE" -> event.withData<OpCode0.ChannelInfo> {
                    processChannelDeleteEvent(originType, this)
                }
                // 频道用户新增
                "GUILD_MEMBER_ADD" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberAddEvent(originType, this)
                }
                // 频道用户更新
                "GUILD_MEMBER_UPDATE" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberUpdateEvent(originType, this)
                }
                // 频道用户离开
                "GUILD_MEMBER_REMOVE" -> event.withData<OpCode0.GuildMember> {
                    processGuildMemberRemoveEvent(originType, this)
                }
                // 用户进入音视频/直播子频道时
                "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER" -> event.withData<OpCode0.AudioLiveChannelMember> {
                    processAudioLiveChannelEnterEvent(originType, this)
                }
                // 用户离开音视频/直播子频道时
                "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT" -> event.withData<OpCode0.AudioLiveChannelMember> {
                    processAudioLiveChannelExitEvent(originType, this)
                }
                // 机器人加入群聊
                "GROUP_ADD_ROBOT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupAddRobotEvent(originType, this)
                }
                // 机器人退出群聊
                "GROUP_DEL_ROBOT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupDelRobotEvent(originType, this)
                }
                // 群聊拒绝机器人主动消息
                "GROUP_MSG_REJECT" -> event.withData<OpCode0.GroupRobot> {
                    processGroupMsgRejectEvent(originType, this)
                }
                // 群聊接受机器人主动消息
                "GROUP_MSG_RECEIVE" -> event.withData<OpCode0.GroupRobot> {
                    processGroupMsgReceiveEvent(originType, this)
                }
                // 用户添加机器人
                "FRIEND_ADD" -> event.withData<OpCode0.UserRobot> {
                    processFriendAddEvent(originType, this)
                }
                // 用户删除机器人
                "FRIEND_DEL" -> event.withData<OpCode0.UserRobot> {
                    processFriendDelEvent(originType, this)
                }
                // 用户拒绝机器人主动消息
                "C2C_MSG_REJECT" -> event.withData<OpCode0.UserRobot> {
                    processC2CMsgRejectEvent(originType, this)
                }
                // 用户允许机器人主动消息
                "C2C_MSG_RECEIVE" -> event.withData<OpCode0.UserRobot> {
                    processC2CMsgReceiveEvent(originType, this)
                }
            }
            else -> { }
        }
    }

    protected abstract suspend fun processGuildMessageEvent(originType: String, message: OpCode0.GuildMessage)
    protected abstract suspend fun processGroupMessageEvent(originType: String, message: OpCode0.GroupMessage)
    protected abstract suspend fun processC2CMessageEvent(originType: String, message: OpCode0.C2CMessage)
    protected abstract suspend fun processGuildCreateEvent(originType: String, message: OpCode0.GuildInfo)
    protected abstract suspend fun processGuildUpdateEvent(originType: String, message: OpCode0.GuildInfo)
    protected abstract suspend fun processGuildDeleteEvent(originType: String, message: OpCode0.GuildInfo)
    protected abstract suspend fun processChannelCreateEvent(originType: String, message: OpCode0.ChannelInfo)
    protected abstract suspend fun processChannelUpdateEvent(originType: String, message: OpCode0.ChannelInfo)
    protected abstract suspend fun processChannelDeleteEvent(originType: String, message: OpCode0.ChannelInfo)
    protected abstract suspend fun processGuildMemberAddEvent(originType: String, message: OpCode0.GuildMember)
    protected abstract suspend fun processGuildMemberUpdateEvent(originType: String, message: OpCode0.GuildMember)
    protected abstract suspend fun processGuildMemberRemoveEvent(originType: String, message: OpCode0.GuildMember)
    protected abstract suspend fun processAudioLiveChannelEnterEvent(originType: String, message: OpCode0.AudioLiveChannelMember)
    protected abstract suspend fun processAudioLiveChannelExitEvent(originType: String, message: OpCode0.AudioLiveChannelMember)
    protected abstract suspend fun processGroupAddRobotEvent(originType: String, message: OpCode0.GroupRobot)
    protected abstract suspend fun processGroupDelRobotEvent(originType: String, message: OpCode0.GroupRobot)
    protected abstract suspend fun processGroupMsgRejectEvent(originType: String, message: OpCode0.GroupRobot)
    protected abstract suspend fun processGroupMsgReceiveEvent(originType: String, message: OpCode0.GroupRobot)
    protected abstract suspend fun processFriendAddEvent(originType: String, message: OpCode0.UserRobot)
    protected abstract suspend fun processFriendDelEvent(originType: String, message: OpCode0.UserRobot)
    protected abstract suspend fun processC2CMsgRejectEvent(originType: String, message: OpCode0.UserRobot)
    protected abstract suspend fun processC2CMsgReceiveEvent(originType: String, message: OpCode0.UserRobot)

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