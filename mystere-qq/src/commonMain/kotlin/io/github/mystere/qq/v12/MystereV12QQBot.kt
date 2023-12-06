package io.github.mystere.qq.v12

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.v12.IOneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.OneBotV12ActionResp
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import io.github.mystere.onebot.v12.cqcode.CQCodeV12MessageItem
import io.github.mystere.onebot.v12.cqcode.encodeToString
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.CQCode
import io.github.mystere.serialization.cqcode.plus
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

class MystereV12QQBot(
    config: QQBot.Config,
    connection: IOneBotV12Connection,
): IMystereQQBot<OneBotV12Action, IOneBotV12Event, OneBotV12ActionResp>(config, connection) {
    override val log: KLogger = KotlinLogging.logger("MystereV12QQBot(botId: ${config.appId})")

    override suspend fun processGuildMessage(originType: String, message: OpCode0.GuildMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupMessage(originType: String, message: OpCode0.GroupMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processC2CMessage(originType: String, message: OpCode0.C2CMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupAddRobot(originType: String, message: OpCode0.GroupAddRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun onProcessOneBotActionInternalError(e: Throwable, originAction: OneBotV12Action) {
        OneBotConnection.response(OneBotV12ActionResp(
            status = IOneBotActionResp.Status.failed,
            retcode = OneBotV12ActionResp.RetCode.InternalHandlerError,
            message = "Mystere internal error.",
            echo = originAction.echo,
        ))
    }

    override suspend fun IOneBotV12Event.encodeToJsonElement(): JsonElement {
        return MystereJson.encodeToJsonElement(this)
    }

    override suspend fun processOneBotAction(action: OneBotV12Action) {
        when (action.action) {
            OneBotV12Action.Action.send_private_msg -> TODO()
            OneBotV12Action.Action.send_guild_channel_msg -> TODO()
        }
    }
}