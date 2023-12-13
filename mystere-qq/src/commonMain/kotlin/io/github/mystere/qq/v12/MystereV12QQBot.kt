package io.github.mystere.qq.v12

import app.cash.sqldelight.EnumColumnAdapter
import io.github.mystere.core.sqlite.createSqliteDriver
import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.OneBotException
import io.github.mystere.onebot.v12.OneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.OneBotV12ActionResp
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.*
import io.github.mystere.qq.database.IQQDatabase
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.dto.QQCodeMessageDataDto
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement

class MystereV12QQBot(
    config: QQBot.Config,
    connection: IOneBotV12Connection,
): IMystereQQBot<OneBotV12Action, OneBotV12Event, OneBotV12ActionResp>(config, connection) {
    override val log: KLogger = KotlinLogging.logger("MystereV12QQBot(botId: ${config.appId})")
    override val QQDatabase: IQQDatabase by lazy {
        IQQDatabase(
            driver = createSqliteDriver(
                IQQDatabase.Schema, "qq_${connection.versionName}_${config.appId}.db"
            ),
            messageAdapter = Message.Adapter(
                typeAdapter = EnumColumnAdapter(),
            ),
        )
    }

    override suspend fun processGuildMessageEvent(originType: String, message: OpCode0.GuildMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGroupMessageEvent(originType: String, message: OpCode0.GroupMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processC2CMessageEvent(originType: String, message: OpCode0.C2CMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun processGuildEvent(eventType: GuildEventType, message: OpCode0.GuildInfo) {
        OneBotConnection.send(OneBotV12Event(
            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.guild,
            subType = eventType,
            params = OneBotV12QQEvent.GuildEvent(
                description = message.description,
                icon = message.icon,
                joinedAt = message.joinedAt,
                maxMembers = message.maxMembers,
                name = message.name,
                opUserId = message.opUserId,
                memberCount = message.memberCount,
                ownerId = message.ownerId,
            ),
            serializer = OneBotV12QQEvent.GuildEvent.serializer()
        ))
    }

    override suspend fun processChannelEvent(eventType: ChannelEventType, message: OpCode0.ChannelInfo) {
        OneBotConnection.send(OneBotV12Event(
            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.channel,
            subType = eventType,
            params = OneBotV12QQEvent.ChannelEvent(
                guildId = message.guildId,
                name = message.name,
                opUserId = message.opUserId,
                ownerId = message.ownerId,
                channelSubType = message.subType,
                channelType = message.type,
            ),
            serializer = OneBotV12QQEvent.ChannelEvent.serializer()
        ))
    }

    override suspend fun processGuildMemberEvent(eventType: GuildMemberEventType, message: OpCode0.GuildMember) {
        OneBotConnection.send(OneBotV12Event(
//            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.guild_member,
            subType = eventType,
            params = OneBotV12QQEvent.GuildMemberEvent(
                guildId = message.guildId,
                user = message.user,
                nick = message.nick,
                roles = message.roles,
                joinedAt = message.joinedAt,
                opUserId = message.opUserId,
            ),
            serializer = OneBotV12QQEvent.GuildMemberEvent.serializer()
        ))
    }

    override suspend fun processAudioLiveChannelEvent(eventType: AudioLiveChannelEventType, message: OpCode0.AudioLiveChannelMember) {
        OneBotConnection.send(OneBotV12Event(
//            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.audio_live_channel,
            subType = eventType,
            params = OneBotV12QQEvent.AudioLiveChannelMemberEvent(
                guildId = message.guildId,
                channelId = message.channelId,
                channelType = message.channelType,
                userId = message.userId,
            ),
            serializer = OneBotV12QQEvent.AudioLiveChannelMemberEvent.serializer()
        ))
    }


    override suspend fun processGroupRobotEvent(eventType: GroupRobotEventType, message: OpCode0.GroupRobot) {
        OneBotConnection.send(OneBotV12Event(
//            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.group_robot,
            subType = eventType,
            params = OneBotV12QQEvent.GroupRobotEvent(
                timestamp = message.timestamp,
                groupOpenid = message.groupOpenid,
                opMemberOpenid = message.opMemberOpenid,
            ),
            serializer = OneBotV12QQEvent.GroupRobotEvent.serializer()
        ))
    }

    override suspend fun processUserRobotEvent(eventType: UserRobotEventType, message: OpCode0.UserRobot) {
        OneBotConnection.send(OneBotV12Event(
//            id = message.id,
            selfId = config.appId,
            type = OneBotV12Event.Type.notice,
            detailType = QQNoticeType.user_robot,
            subType = eventType,
            params = OneBotV12QQEvent.UserRobotEvent(
                timestamp = message.timestamp,
                openid = message.openid,
            ),
            serializer = OneBotV12QQEvent.UserRobotEvent.serializer()
        ))
    }

    override suspend fun processMessageReactionEvent(
        eventType: MessageReactionEventType,
        message: OpCode0.MessageReaction
    ) {
        TODO("Not yet implemented")
    }

    override fun createSuccessResp(data: JsonElement?, echo: JsonElement?): OneBotV12ActionResp {
        TODO("Not yet implemented")
    }

    override fun createFailedResp(e: OneBotException): OneBotV12ActionResp {
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
                is QQCodeMessageDataDto ->
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