package io.github.mystere.qq.v11

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.v11.IOneBotV11Event
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.onebot.v11.OneBotV11Referer
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.onebot.v11.cqcode.encodeToString
import io.github.mystere.onebot.v11.withParams
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.dto.MessageReference
import io.github.mystere.qqsdk.qqapi.http.channelsMessage
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.CQCode
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import io.github.mystere.serialization.cqcode.plus

class MystereV11QQBot(
    config: QQBot.Config,
    connectionConfig: IOneBotV11Connection.IConfig,
): IMystereQQBot<OneBotV11Action, IOneBotV11Event>(config, connectionConfig) {
    override val log: KLogger = KotlinLogging.logger("MystereV11QQBot(botId: ${config.appId})")

    override suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (event.type) {
                "MESSAGE_CREATE", "AT_MESSAGE_CREATE" -> event.withData<OpCode0.AtMessageCreate> {
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
                    sendOneBotEvent(IOneBotV11Event.Message(
                        selfId = config.appId,
                        messageType = IOneBotV11Event.MessageType.guild,
                        subType = IOneBotV11Event.MessageSubType.channel,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        sender = IOneBotV11Event.Message.Sender(
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

    override suspend fun IOneBotV11Event.encodeToJsonElement(): JsonElement {
        return MystereJson.encodeToJsonElement(this)
    }

    override suspend fun processOneBotAction(action: OneBotV11Action) {
        when (action.action) {
            OneBotV11Action.Action.send_private_msg -> action.withParams<OneBotV11Action.SendPrivateMsg> {

            }
            OneBotV11Action.Action.send_guild_channel_msg -> action.withParams<OneBotV11Action.SendGuildChannelMsg> {
                QQBotApi.channelsMessage(
                    channelId = channelId,
                    content = message.asQQMessageContent(),
                    images = message.asQQImageList(),
                    messageReference = message.asQQMessageReference(),
                    msgId = replyMsgId,
                )
            }
        }
    }
}