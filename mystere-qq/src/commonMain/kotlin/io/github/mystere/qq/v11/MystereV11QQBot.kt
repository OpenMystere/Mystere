package io.github.mystere.qq.v11

import io.github.mystere.onebot.v11.IOneBotV11Event
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.CQCode
import io.github.mystere.serialization.cqcode.CQCodeMessage
import io.github.mystere.serialization.cqcode.plus
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class MystereV11QQBot(
    config: QQBot.Config,
    connectionConfig: IOneBotV11Connection.IConfig,
): IMystereQQBot<OneBotV11Action>(config, connectionConfig) {
    override val log: KLogger = KotlinLogging.logger("MystereV11QQBot(botId: ${config.appId})")

    override suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (event.type) {
                "AT_MESSAGE_CREATE" -> event.withData<OpCode0.AtMessageCreate> {
                    val cqMsg: CQCodeMessage = with(this) {
                        var message: CQCodeMessage? = null
                        for (item in content.asV11MessageContent()) {
                            message = message.plus(item)
                        }
                        for (attachment in attachments) {
                            if (attachment.contentType.startsWith("image")) {
                                message = message.plus(
                                    CQCodeV11MessageItem.Image(
                                        file = attachment.url,
                                        url = attachment.url,
                                    )
                                )
                            }
                        }
                        return@with message!!
                    }
                    sendOneBotEvent(IOneBotV11Event.Message(
                        selfId = config.appId,
                        messageType = IOneBotV11Event.MessageType.guild,
                        subType = IOneBotV11Event.MessageSubType.channel,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        font = 0,
                        sender = IOneBotV11Event.Message.Sender(
                            userId = author.id
                        ),
                        guildId = guildId,
                        channelId = channelId,
                        userId = author.id
                    ), IOneBotV11Event.Message.serializer())
                }
            }
            else -> { }
        }
    }

    override suspend fun processOneBotAction(action: OneBotV11Action) {
        when (action.action) {
            OneBotV11Action.Action.send_private_msg -> TODO()
            OneBotV11Action.Action.send_guild_channel_msg -> TODO()
        }
    }
}