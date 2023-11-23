package io.github.mystere.qq.v12

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.qq.IMystereQQBot
import io.github.mystere.onebot.v12.IOneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import io.github.mystere.onebot.v12.cqcode.CQCodeV12MessageItem
import io.github.mystere.onebot.v12.cqcode.encodeToString
import io.github.mystere.qq.v11.asV11MessageContent
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.CQCode
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.JsonElement
import io.github.mystere.serialization.cqcode.plus
import kotlinx.serialization.json.encodeToJsonElement

class MystereV12QQBot(
    config: QQBot.Config,
    connectionConfig: IOneBotV12Connection.IConfig,
): IMystereQQBot<OneBotV12Action, IOneBotV12Event>(config, connectionConfig) {
    override val log: KLogger = KotlinLogging.logger("MystereV12QQBot(botId: ${config.appId})")

    override suspend fun processQQEvent(event: QQBotWebsocketPayload) {
        when (event.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (event.type) {
                "MESSAGE_CREATE" -> event.withData<OpCode0.AtMessageCreate> {
                    val cqMsg: CQCodeV12Message = with(this) {
                        var message: CQCodeV12Message? = null
//                        var firstAtSelfFilted = false
                        for (item in content.asV12MessageContent()) {
//                            if (item is CQCodeV12MessageItem.Mention &&
//                                item.userId == botUser?.id && !firstAtSelfFilted) {
//                                // 因为触发机器人必须 at，所以这里过滤掉第一次 at 机器人
//                                firstAtSelfFilted = true
//                                continue
//                            }
                            message += item
                        }
                        for (attachment in attachments) {
                            if (attachment.contentType.startsWith("image")) {
                                message += CQCodeV12MessageItem.Image(
                                    fileId = "",
                                )
                            }
                        }
                        return@with message!!
                    }
                    sendOneBotEvent(IOneBotV12Event.Message(
                        selfId = config.appId,
                        messageType = IOneBotV12Event.MessageType.guild,
                        subType = IOneBotV12Event.MessageSubType.channel,
                        messageId = id,
                        message = cqMsg,
                        rawMessage = CQCode.encodeToString(cqMsg),
                        font = 0,
                        sender = IOneBotV12Event.Message.Sender(
                            userId = author.id
                        ),
                        guildId = guildId,
                        channelId = channelId,
                        userId = author.id
                    ))
                }
            }
            else -> { }
        }
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