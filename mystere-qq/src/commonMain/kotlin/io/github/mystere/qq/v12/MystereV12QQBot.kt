package io.github.mystere.qq.v12

import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.v12.IOneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.OneBotV12ActionResp
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.dto.CodeMessageDataDto
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement

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

    override suspend fun onProcessInternalError(e: Throwable, originAction: OneBotV12Action): OneBotV12ActionResp {
        return OneBotV12ActionResp(
            status = IOneBotActionResp.Status.failed,
            retcode = OneBotV12ActionResp.RetCode.InternalHandlerError,
            message = "Mystere internal error.",
            echo = originAction.echo,
        )
    }

    override suspend fun processOneBotAction(action: OneBotV12Action): OneBotV12ActionResp {
        try {
            return processOneBotAction(action.rawAction, action.params, action.echo)
                ?: OneBotV12ActionResp(
                    status = IOneBotActionResp.Status.failed,
                    retcode = OneBotV12ActionResp.RetCode.UnsupportedAction,
                    echo = action.echo,
                )
        } catch (e1: Throwable) {
            when (e1) {
                is CodeMessageDataDto ->
                    log.warn { "send request to qq openapi failed (code: ${e1.code}): ${e1.message}" }
                else ->
                    log.warn(e1) { "action process error: ${e1.message}" }
            }
            return OneBotV12ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV12ActionResp.RetCode.BadHandler,
                message = e1.message ?: "unknown error.",
                echo = action.echo,
            )
        }
    }

    private suspend fun processOneBotAction(rawAction: String, params: OneBotV12Action.Param, echo: JsonElement?): OneBotV12ActionResp? {
        return when (params) {
            is OneBotV12Action.SendMessage -> processSendMessage(params, echo)
            is OneBotV12Action.DeleteMessage -> processDeleteMessage(params, echo)
            else -> return null
        }
    }

    private suspend fun processSendMessage(params: OneBotV12Action.SendMessage, echo: JsonElement?): OneBotV12ActionResp {
        TODO()
    }

    private suspend fun processDeleteMessage(params: OneBotV12Action.DeleteMessage, echo: JsonElement?): OneBotV12ActionResp {
        TODO()
    }
}