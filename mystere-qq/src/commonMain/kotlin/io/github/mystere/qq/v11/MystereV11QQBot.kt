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
import io.github.mystere.qqsdk.qqapi.dto.CodeMessageDataDto
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
                "MESSAGE_CREATE", "AT_MESSAGE_CREATE" -> event.withData<OpCode0.Message> {
                    val cqMsg: CQCodeV11Message = with(this) {
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
                        return@with message
                    } ?: return@withData
                    OneBotConnection.send(IOneBotV11Event.Message(
                        id = id,
                        selfId = config.appId,
                        messageType = IOneBotV11Event.MessageType.guild,
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
            }
            else -> { }
        }
    }

    override suspend fun processOneBotAction(action: OneBotV11Action) {
        try {
            when (val params = action.params) {
                is OneBotV11Action.SendPrivateMsg -> with(params) {

                }
                is OneBotV11Action.SendGuildChannelMsg -> with(params) {
                    var originMessageId: String? = null
                    var originEventId: String? = null
                    if (originEvent != null) {
                        if (originEvent!!.type == IOneBotV11Event.PostType.message) {
                            log.debug { "send passive message,reply message_id: ${originEvent!!.id}" }
                            originMessageId = originEvent!!.id
                        } else {
                            log.debug { "send passive message, reply event_id: ${originEvent!!.id}" }
                            originEventId = originEvent!!.id
                        }
                    } else {
                        log.debug { "send proactive message" }
                    }
                    val result = QQBotApi.messageIO_channel(
                        channelId = channelId,
//                        msgType = 0,
                        content = message.asQQMessageContent(),
                        images = message.asQQImageList(),
                        messageReference = message.asQQMessageReference(),
                        msgId = originMessageId,
                        eventId = originEventId,
                    )
                    OneBotConnection.response(OneBotV11ActionResp(
                        status = IOneBotActionResp.Status.ok,
                        retcode = OneBotV11ActionResp.RetCode.OK,
                        data = OneBotV11ActionResp.MessageIdResp(
                            messageId = result.id,
                        ),
                        echo = action.echo,
                    ))
                }
                else -> { }
            }
        } catch (e: CodeMessageDataDto) {
            log.error(e) { "send request to qq openapi failed (code: ${e.code}): ${e.message}" }
            OneBotConnection.response(OneBotV11ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV11ActionResp.RetCode.OK,
                message = e.message,
                echo = action.echo,
            ))
        }
    }

    override suspend fun onProcessOneBotActionInternalError(e: Exception, originAction: OneBotV11Action) {
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
}