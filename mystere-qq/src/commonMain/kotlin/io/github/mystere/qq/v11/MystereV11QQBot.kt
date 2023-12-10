package io.github.mystere.qq.v11

import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.v11.OneBotV11Event
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.onebot.v11.cqcode.encodeToString
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.data.MessageAttachment
import io.github.mystere.qqsdk.qqapi.dto.CodeMessageDataDto
import io.github.mystere.qqsdk.qqapi.dto.GroupMessageRequestDto
import io.github.mystere.qqsdk.qqapi.http.messageIO_channel
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.serialization.cqcode.CQCode
import io.github.mystere.serialization.cqcode.plus
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement

class MystereV11QQBot internal constructor(
    config: QQBot.Config,
    connection: IOneBotV11Connection,
): IMystereQQBot<OneBotV11Action, OneBotV11Event, OneBotV11ActionResp>(config, connection) {
    override val log: KLogger = KotlinLogging.logger("MystereV11QQBot(botId: ${config.appId})")

    override suspend fun processGuildMessageEvent(originType: String, message: OpCode0.GuildMessage) {
        val cqMsg: CQCodeV11Message = parseAsV11Message(message.content, message.attachments)
            ?: return
        OneBotConnection.send(OneBotV11Event(
            id = message.id,
            selfId = botId,
            postType = OneBotV11Event.PostType.message,
            params = OneBotV11Event.Message(
                messageType =
                if (originType == "DIRECT_MESSAGE_CREATE")
                    OneBotV11Event.MessageType.private
                else
                    OneBotV11Event.MessageType.guild,
                subType = OneBotV11Event.MessageSubType.channel,
                messageId = message.id,
                message = cqMsg,
                rawMessage = CQCode.encodeToString(cqMsg),
                sender = OneBotV11Event.Sender(
                    userId = message.author.id,
                    tinyId = message.author.id,
                    nickname = message.author.username,
                ),
                selfTinyId = botId,
                guildId = message.guildId,
                channelId = message.channelId,
                userId = message.author.id,
            )
        ))
    }

    override suspend fun processGroupMessageEvent(originType: String, message: OpCode0.GroupMessage) {
        val cqMsg: CQCodeV11Message = parseAsV11Message(message.content, message.attachments)
            ?: return
        OneBotConnection.send(OneBotV11Event(
            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.message,
            params = OneBotV11Event.Message(
                messageType = OneBotV11Event.MessageType.guild,
                subType = OneBotV11Event.MessageSubType.group,
                messageId = message.id,
                message = cqMsg,
                rawMessage = CQCode.encodeToString(cqMsg),
                sender = OneBotV11Event.Sender(
                    userId = message.author.memberOpenid,
                ),
                userId = message.author.memberOpenid,
                groupId = message.groupOpenid,
            )
        ))
    }

    override suspend fun processC2CMessageEvent(originType: String, message: OpCode0.C2CMessage) {
        val cqMsg: CQCodeV11Message = parseAsV11Message(message.content, message.attachments)
            ?: return
        OneBotConnection.send(OneBotV11Event(
            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.message,
            params = OneBotV11Event.Message(
                messageType = OneBotV11Event.MessageType.private,
                subType = OneBotV11Event.MessageSubType.friend,
                messageId = message.id,
                message = cqMsg,
                rawMessage = CQCode.encodeToString(cqMsg),
                sender = OneBotV11Event.Sender(
                    userId = message.author.userOpenid,
                ),
                userId = message.author.userOpenid,
            )
        ))
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

    override suspend fun processAudioLiveChannelEnterEvent(originType: String, message: OpCode0.AudioLiveChannelMember) {
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

    private suspend fun processOneBotAction(rawAction: String, params: OneBotV11Action.Param, echo: JsonElement?): OneBotV11ActionResp? {
        return when (params) {
            is OneBotV11Action.SendGroupMsg -> processSendGroupMsgAction(params, echo)
            is OneBotV11Action.SendGuildChannelMsg -> processSendGuildChannelMsgAction(params, echo)
            else -> return null
        }
    }

    private suspend fun processSendGroupMsgAction(params: OneBotV11Action.SendGroupMsg, echo: JsonElement?): OneBotV11ActionResp {
        var originMessageId: String? = null
        var originEventId: String? = null
        if (params.originEvent != null) {
            if (params.originEvent!!.type == OneBotV11Event.PostType.message) {
                log.debug { "send passive message, reply message_id: ${params.originEvent!!.id}" }
                originMessageId = params.originEvent!!.id
            } else {
                log.debug { "send passive message, reply event_id: ${params.originEvent!!.id}" }
                originEventId = params.originEvent!!.id
            }
        } else {
            log.debug { "send proactive message" }
        }
        params.message.asQQImageList()
        val result = QQBotApi.messageIO_groups(
            groupOpenId = params.groupId,
            message = GroupMessageRequestDto(
                content = params.message.asQQMessageContent(),
                msgType = 0,
                msgId = originMessageId,
                eventId = originEventId,
            )
        )
        return OneBotV11ActionResp(
            status = IOneBotActionResp.Status.ok,
            retcode = OneBotV11ActionResp.RetCode.OK,
            data = OneBotV11ActionResp.MessageIdResp(
                messageId = result.id,
            ),
            echo = echo,
        )
    }

    private suspend fun processSendGuildChannelMsgAction(params: OneBotV11Action.SendGuildChannelMsg, echo: JsonElement?): OneBotV11ActionResp {
        var originMessageId: String? = null
        var originEventId: String? = null
        if (params.originEvent != null) {
            if (params.originEvent!!.type == OneBotV11Event.PostType.message) {
                log.debug { "send passive message, reply message_id: ${params.originEvent!!.id}" }
                originMessageId = params.originEvent!!.id
            } else {
                log.debug { "send passive message, reply event_id: ${params.originEvent!!.id}" }
                originEventId = params.originEvent!!.id
            }
        } else {
            log.debug { "send proactive message" }
        }
        val result = QQBotApi.messageIO_channel(
            channelId = params.channelId,
            content = params.message.asQQMessageContent(),
            images = params.message.asQQImageList(),
            messageReference = params.message.asQQMessageReference(),
            msgId = originMessageId,
            eventId = originEventId,
        )
        return OneBotV11ActionResp(
            status = IOneBotActionResp.Status.ok,
            retcode = OneBotV11ActionResp.RetCode.OK,
            data = OneBotV11ActionResp.MessageIdResp(
                messageId = result.id,
            ),
            echo = echo,
        )
    }

    override suspend fun processOneBotAction(action: OneBotV11Action): OneBotV11ActionResp {
        try {
            return processOneBotAction(action.rawAction, action.params, action.echo)
                ?: OneBotV11ActionResp(
                    status = IOneBotActionResp.Status.failed,
                    retcode = OneBotV11ActionResp.RetCode.NotFound,
                    echo = action.echo,
                )
        } catch (e1: Throwable) {
            when (e1) {
                is CodeMessageDataDto ->
                    log.warn { "send request to qq openapi failed (code: ${e1.code}): ${e1.message}" }
                else ->
                    log.warn(e1) { "action process error: ${e1.message}" }
            }
            return OneBotV11ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV11ActionResp.RetCode.OK,
                message = e1.message ?: "unknown error.",
                echo = action.echo,
            )
        }
    }

    override suspend fun onProcessInternalError(e: Throwable, originAction: OneBotV11Action): OneBotV11ActionResp {
        return OneBotV11ActionResp(
            status = IOneBotActionResp.Status.failed,
            retcode = OneBotV11ActionResp.RetCode.OK,
            message = "Mystere internal error.",
            echo = originAction.echo,
        )
    }

    private fun parseAsV11Message(content: String, attachments: List<MessageAttachment>): CQCodeV11Message? {
        var message: CQCodeV11Message? = null
        for (item in content.asV11MessageContent()) {
            message += item
        }
        for (attachment in attachments) {
            if (attachment.contentType.startsWith("image")) {
                message += CQCodeV11MessageItem.Image(
                    file = attachment.url,
                    url = attachment.url,
                )
            }
        }
        return message
    }
}