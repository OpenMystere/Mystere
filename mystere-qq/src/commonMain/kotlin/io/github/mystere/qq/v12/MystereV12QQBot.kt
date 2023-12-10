package io.github.mystere.qq.v12

import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.v12.OneBotV12Event
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
): IMystereQQBot<OneBotV12Action, OneBotV12Event, OneBotV12ActionResp>(config, connection) {
    override val log: KLogger = KotlinLogging.logger("MystereV12QQBot(botId: ${config.appId})")

    override suspend fun processGuildMessageEvent(originType: String, message: OpCode0.GuildMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupMessageEvent(originType: String, message: OpCode0.GroupMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processC2CMessageEvent(originType: String, message: OpCode0.C2CMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildCreateEvent(originType: String, message: OpCode0.GuildInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildUpdateEvent(originType: String, message: OpCode0.GuildInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildDeleteEvent(originType: String, message: OpCode0.GuildInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processChannelCreateEvent(originType: String, message: OpCode0.ChannelInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processChannelUpdateEvent(originType: String, message: OpCode0.ChannelInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processChannelDeleteEvent(originType: String, message: OpCode0.ChannelInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildMemberAddEvent(originType: String, message: OpCode0.GuildMember) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildMemberUpdateEvent(originType: String, message: OpCode0.GuildMember) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildMemberRemoveEvent(originType: String, message: OpCode0.GuildMember) {
        TODO("Not yet implemented")
    }

    override suspend fun processAudioLiveChannelEnterEvent(
        originType: String,
        message: OpCode0.AudioLiveChannelMember
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun processAudioLiveChannelExitEvent(originType: String, message: OpCode0.AudioLiveChannelMember) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupAddRobotEvent(originType: String, message: OpCode0.GroupRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupDelRobotEvent(originType: String, message: OpCode0.GroupRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupMsgRejectEvent(originType: String, message: OpCode0.GroupRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupMsgReceiveEvent(originType: String, message: OpCode0.GroupRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processFriendAddEvent(originType: String, message: OpCode0.UserRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processFriendDelEvent(originType: String, message: OpCode0.UserRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processC2CMsgRejectEvent(originType: String, message: OpCode0.UserRobot) {
        TODO("Not yet implemented")
    }

    override suspend fun processC2CMsgReceiveEvent(originType: String, message: OpCode0.UserRobot) {
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