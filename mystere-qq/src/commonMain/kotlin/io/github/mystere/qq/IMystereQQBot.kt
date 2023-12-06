package io.github.mystere.qq

import io.github.mystere.core.IMystereBotConnection
import io.github.mystere.onebot.*
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.v11.MystereV11QQBot
import io.github.mystere.qq.v12.MystereV12QQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.http.IQQBotAPI
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

abstract class IMystereQQBot<ActionT: IOneBotAction, EventT: IOneBotEvent, RespT: IOneBotActionResp> protected constructor(
    protected val config: QQBot.Config,
    connection: IOneBotConnection<ActionT, EventT, RespT>,
): IOneBot<EventT, ActionT, RespT>(config.appId, connection) {
    protected var botUser: OpCode0.Ready.User? = null

    private val mQQBot: QQBot by lazy { QQBot.create(config) }
    protected val QQBotApi: IQQBotAPI get() = mQQBot.BotAPI
    final override suspend fun connect() {
        super.connect()
        coroutineScope.launch(Dispatchers.IO) {
            mQQBot.connect()
        }

        coroutineScope.launch(Dispatchers.IO) {
            for (payload: QQBotWebsocketPayload in mQQBot) {
                try {
                    if (payload.opCode == QQBotWebsocketPayload.OpCode.Dispatch && payload.type == "READY") {
                        payload.withData<OpCode0.Ready> {
                            botUser = user
                        }
                    } else {
                        processQQEvent(payload)
                    }
                } catch (e: Throwable) {
                    log.warn(e) { "process qq event error" }
                }
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            for ((action, resp) in OneBotConnection) {
                try {
                    processOneBotAction(action)
                } catch (e1: Throwable) {
                    log.warn(e1) { "process onebot action error" }
                    try {
                        onProcessOneBotActionInternalError(e1, action)
                    } catch (e2: Throwable) {
                        log.warn(e2) { "error during handle error" }
                    }
                }
            }
        }
    }

    protected abstract suspend fun onProcessOneBotActionInternalError(e: Throwable, originAction: ActionT): OneBotV11ActionResp

    private suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (val originType = event.type) {
                // 频道全量消息
                "MESSAGE_CREATE",
                // 频道@消息
                "AT_MESSAGE_CREATE",
                // 频道私聊消息
                "DIRECT_MESSAGE_CREATE", -> event.withData<OpCode0.GuildMessage> {
                    processGuildMessage(originType, this)
                }
                // 群@消息
                "GROUP_AT_MESSAGE_CREATE" -> event.withData<OpCode0.GroupMessage> {
                    processGroupMessage(originType, this)
                }
                // 单聊消息
                "C2C_MESSAGE_CREATE" -> event.withData<OpCode0.C2CMessage> {
                    processC2CMessage(originType, this)
                }
                // 机器人被添加到群聊
                "GROUP_ADD_ROBOT" -> event.withData<OpCode0.GroupAddRobot> {
                    processGroupAddRobot(originType, this)
                }
            }
            else -> { }
        }
    }

    protected abstract suspend fun processGuildMessage(originType: String, message: OpCode0.GuildMessage)
    protected abstract suspend fun processGroupMessage(originType: String, message: OpCode0.GroupMessage)
    protected abstract suspend fun processC2CMessage(originType: String, message: OpCode0.C2CMessage)
    protected abstract suspend fun processGroupAddRobot(originType: String, message: OpCode0.GroupAddRobot)

    protected open suspend fun EventT.encodeToJsonElement(): JsonElement {
        throw NotImplementedError("Please implement \"fun EventT.encodeToJsonElement(): JsonElement\"" +
                " in your instance of IMystereQQBot!")
    }

    protected abstract suspend fun processOneBotAction(action: ActionT): RespT

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