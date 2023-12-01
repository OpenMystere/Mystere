package io.github.mystere.qq.v11

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.v11.IOneBotV11Event
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
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.CQCode
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import io.github.mystere.serialization.cqcode.plus

class MystereV11QQBot internal constructor(
    config: QQBot.Config,
    connection: IOneBotV11Connection,
): IMystereQQBot<OneBotV11Action, IOneBotV11Event, OneBotV11ActionResp>(config, connection) {
    override val log: KLogger = KotlinLogging.logger("MystereV11QQBot(botId: ${config.appId})")

    override suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (event.type) {
                // 频道全量消息
                "MESSAGE_CREATE",
                // 频道@消息
                "AT_MESSAGE_CREATE",
                // 频道私聊消息
                "DIRECT_MESSAGE_CREATE", -> event.withData<OpCode0.GuildMessage> {
                    val cqMsg: CQCodeV11Message = parseAsV11Message(content, attachments)
                        ?: return@withData
                    OneBotConnection.send(IOneBotV11Event.Message(
                        id = id,
                        selfId = config.appId,
                        messageType =
                            if (event.type == "DIRECT_MESSAGE_CREATE")
                                IOneBotV11Event.MessageType.guild
                            else
                                IOneBotV11Event.MessageType.private,
                        subType = IOneBotV11Event.MessageSubType.channel,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        sender = IOneBotV11Event.Sender(
                            userId = author.id,
                            tinyId = author.id,
                        ),
                        selfTinyId = author.id,
                        guildId = guildId,
                        channelId = channelId,
                        userId = author.id,
                    ))
                }
                // 群@消息
                "GROUP_AT_MESSAGE_CREATE" -> event.withData<OpCode0.GroupMessage> {
                    val cqMsg: CQCodeV11Message = parseAsV11Message(content, attachments)
                        ?: return@withData
                    OneBotConnection.send(IOneBotV11Event.Message(
                        id = id,
                        selfId = config.appId,
                        messageType = IOneBotV11Event.MessageType.guild,
                        subType = IOneBotV11Event.MessageSubType.group,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        sender = IOneBotV11Event.Sender(
                            userId = author.memberOpenid,
                        ),
                        userId = author.memberOpenid,
                        groupId = groupOpenid,
                    ))
                }
                // 单聊消息
                "C2C_MESSAGE_CREATE" -> event.withData<OpCode0.C2CMessage> {
                    val cqMsg: CQCodeV11Message = parseAsV11Message(content, attachments)
                        ?: return@withData
                    OneBotConnection.send(IOneBotV11Event.Message(
                        id = id,
                        selfId = config.appId,
                        messageType = IOneBotV11Event.MessageType.private,
                        subType = IOneBotV11Event.MessageSubType.friend,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        sender = IOneBotV11Event.Sender(
                            userId = author.userOpenid,
                        ),
                        userId = author.userOpenid,
                    ))
                }
                // 机器人被添加到群聊
                "GROUP_ADD_ROBOT" -> event.withData<OpCode0.GroupAddRobot> {

                }
            }
            else -> { }
        }
    }

    private suspend fun processOneBotAction(rawAction: String, params: OneBotV11Action.Param, echo: JsonElement?): Boolean {
        when (params) {
            is OneBotV11Action.SendGroupMsg -> {
                processSendGroupMsg(params, echo)
                return true
            }

            is OneBotV11Action.SendGuildChannelMsg -> {
                processSendGuildChannelMsg(params, echo)
                return true
            }
            else -> return false
        }
    }

    private suspend fun processSendGroupMsg(params: OneBotV11Action.SendGroupMsg, echo: JsonElement?) {
        var originMessageId: String? = null
        var originEventId: String? = null
        if (params.originEvent != null) {
            if (params.originEvent!!.type == IOneBotV11Event.PostType.message) {
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
        OneBotConnection.response(OneBotV11ActionResp(
            status = IOneBotActionResp.Status.ok,
            retcode = OneBotV11ActionResp.RetCode.OK,
            data = OneBotV11ActionResp.MessageIdResp(
                messageId = result.id,
            ),
            echo = echo,
        ))
    }

    private suspend fun processSendGuildChannelMsg(params: OneBotV11Action.SendGuildChannelMsg, echo: JsonElement?) {
        var originMessageId: String? = null
        var originEventId: String? = null
        if (params.originEvent != null) {
            if (params.originEvent!!.type == IOneBotV11Event.PostType.message) {
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
//                    msgType = 0,
            content = params.message.asQQMessageContent(),
            images = params.message.asQQImageList(),
            messageReference = params.message.asQQMessageReference(),
            msgId = originMessageId,
            eventId = originEventId,
        )
        OneBotConnection.response(OneBotV11ActionResp(
            status = IOneBotActionResp.Status.ok,
            retcode = OneBotV11ActionResp.RetCode.OK,
            data = OneBotV11ActionResp.MessageIdResp(
                messageId = result.id,
            ),
            echo = echo,
        ))
    }

    override suspend fun processOneBotAction(action: OneBotV11Action) {
        try {
            if (processOneBotAction(action.rawAction, action.params, action.echo)) {
                return
            }
            OneBotConnection.response(OneBotV11ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV11ActionResp.RetCode.NotFound,
                echo = action.echo,
            ))
        } catch (e1: Throwable) {
            when (e1) {
                is CodeMessageDataDto ->
                    log.warn { "send request to qq openapi failed (code: ${e1.code}): ${e1.message}" }
                else ->
                    log.warn(e1) { "action process error: ${e1.message}" }
            }
            OneBotConnection.response(OneBotV11ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV11ActionResp.RetCode.OK,
                message = e1.message ?: "unknown error.",
                echo = action.echo,
            ))
        }
    }

    override suspend fun onProcessOneBotActionInternalError(e: Throwable, originAction: OneBotV11Action) {
        OneBotConnection.response(OneBotV11ActionResp(
            status = IOneBotActionResp.Status.failed,
            retcode = OneBotV11ActionResp.RetCode.OK,
            message = "Mystere internal error.",
            echo = originAction.echo,
        ))
    }

    override suspend fun IOneBotV11Event.encodeToJsonElement(): JsonElement {
        return MystereJson.encodeToJsonElement(this)
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