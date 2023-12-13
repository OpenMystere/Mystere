package io.github.mystere.qq.v11

import app.cash.sqldelight.EnumColumnAdapter
import io.github.mystere.core.sqlite.createSqliteDriver
import io.github.mystere.onebot.IOneBotActionResp
import io.github.mystere.onebot.OneBotException
import io.github.mystere.onebot.v11.OneBotV11Event
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.onebot.v11.cqcode.encodeToJson
import io.github.mystere.onebot.v11.cqcode.encodeToString
import io.github.mystere.qq.*
import io.github.mystere.qq.database.IQQDatabase
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.data.MessageAttachment
import io.github.mystere.qqsdk.qqapi.data.MessageReference
import io.github.mystere.qqsdk.qqapi.dto.QQCodeMessageDataDto
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
        val cqMsg: CQCodeV11Message = parseAsV11Message(
            message.content, message.attachments, message.messageReference
        ) ?: return
        QQDatabase.messageQueries.saveMessage(
            id = message.id,
            content = CQCode.encodeToJson(cqMsg).toString(),
            type = QQMessageContent.Type.guild,
            deleted = false,
        )
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
        val cqMsg: CQCodeV11Message = parseAsV11Message(
            message.content, message.attachments, null
        ) ?: return
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
        val cqMsg: CQCodeV11Message = parseAsV11Message(
            message.content, message.attachments, null
        ) ?: return
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

    override suspend fun processGuildEvent(eventType: GuildEventType, message: OpCode0.GuildInfo) {
        OneBotConnection.send(OneBotV11Event(
            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.GuildEvent(
                subType = eventType,
                description = message.description,
                icon = message.icon,
                joinedAt = message.joinedAt,
                maxMembers = message.maxMembers,
                name = message.name,
                opUserId = message.opUserId,
                memberCount = message.memberCount,
                ownerId = message.ownerId,
            ),
            serializer = OneBotQQEvent.GuildEvent.serializer()
        ))
    }

    override suspend fun processChannelEvent(eventType: ChannelEventType, message: OpCode0.ChannelInfo) {
        OneBotConnection.send(OneBotV11Event(
            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.ChannelEvent(
                subType = eventType,
                guildId = message.guildId,
                name = message.name,
                opUserId = message.opUserId,
                ownerId = message.ownerId,
                channelSubType = message.subType,
                channelType = message.type,
            ),
            serializer = OneBotQQEvent.ChannelEvent.serializer()
        ))
    }

    override suspend fun processGuildMemberEvent(eventType: GuildMemberEventType, message: OpCode0.GuildMember) {
        OneBotConnection.send(OneBotV11Event(
//            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.GuildMemberEvent(
                subType = eventType,
                guildId = message.guildId,
                user = message.user,
                nick = message.nick,
                roles = message.roles,
                joinedAt = message.joinedAt,
                opUserId = message.opUserId,
            ),
            serializer = OneBotQQEvent.GuildMemberEvent.serializer()
        ))
    }

    override suspend fun processAudioLiveChannelEvent(eventType: AudioLiveChannelEventType, message: OpCode0.AudioLiveChannelMember) {
        OneBotConnection.send(OneBotV11Event(
//            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.AudioLiveChannelMemberEvent(
                subType = eventType,
                guildId = message.guildId,
                channelId = message.channelId,
                channelType = message.channelType,
                userId = message.userId,
            ),
            serializer = OneBotQQEvent.AudioLiveChannelMemberEvent.serializer()
        ))
    }

    override suspend fun processGroupRobotEvent(eventType: GroupRobotEventType, message: OpCode0.GroupRobot) {
        OneBotConnection.send(OneBotV11Event(
//            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.GroupRobotEvent(
                subType = eventType,
                timestamp = message.timestamp,
                groupOpenid = message.groupOpenid,
                opMemberOpenid = message.opMemberOpenid,
            ),
            serializer = OneBotQQEvent.GroupRobotEvent.serializer()
        ))
    }

    override suspend fun processUserRobotEvent(eventType: UserRobotEventType, message: OpCode0.UserRobot) {
        OneBotConnection.send(OneBotV11Event(
//            id = message.id,
            selfId = config.appId,
            postType = OneBotV11Event.PostType.notice,
            params = OneBotQQEvent.UserRobotEvent(
                subType = eventType,
                timestamp = message.timestamp,
                openid = message.openid,
            ),
            serializer = OneBotQQEvent.UserRobotEvent.serializer()
        ))
    }

    override suspend fun processMessageReactionEvent(eventType: MessageReactionEventType, message: OpCode0.MessageReaction) {
        TODO("Not yet implemented")
    }

    override fun createSuccessResp(data: JsonElement?, echo: JsonElement?): OneBotV11ActionResp {
        return OneBotV11ActionResp(
            status = IOneBotActionResp.Status.ok,
            retcode = OneBotV11ActionResp.RetCode.OK,
            data = data?.let {
                OneBotV11ActionResp.CustomResp(
                    content = it
                )
            },
            echo = echo,
        )
    }

    override fun createFailedResp(e: OneBotException): OneBotV11ActionResp {
        TODO("Not yet implemented")
    }


    private suspend fun processOneBotAction(rawAction: String, params: OneBotV11Action.Param, echo: JsonElement?): OneBotV11ActionResp? {
        return when (params) {
            is OneBotV11Action.SendGroupMsg -> processSendGroupMsgAction(params, echo)
            is OneBotV11Action.SendGuildChannelMsg -> processSendGuildChannelMsgAction(params, echo)
            is OneBotV11Action.SendMsg ->
                if (params.groupId != null) {
                    processSendGroupMsgAction(OneBotV11Action.SendGroupMsg(
                        groupId = params.groupId!!,
                        message = params.message,
                        messageSeq = params.messageSeq,
                        autoEscape = params.autoEscape,
                        originEvent = params.originEvent,
                    ), echo)
                } else if (params.userId != null) {
                    processSendPrivateMsgAction(OneBotV11Action.SendPrivateMsg(
                        userId = params.userId!!,
                        message = params.message,
                        messageSeq = params.messageSeq,
                        autoEscape = params.autoEscape,
                        originEvent = params.originEvent,
                    ), echo)
                } else {
                    log.warn { "receive send_msg action but no user_id and group_id!" }
                    null
                }
            is OneBotV11Action.CustomAction -> {
                val action: OneBotQQAction = try {
                    OneBotQQAction.valueOf(rawAction.replace("_async", "")
                        .replace("_rate_limited", ""))
                } catch (e: Throwable) {
                    return null
                }
                return processOneBotQQAction(action, params.content, echo)
            }
            else -> null
        }
    }

    private suspend fun processSendPrivateMsgAction(params: OneBotV11Action.SendPrivateMsg, echo: JsonElement?): OneBotV11ActionResp {
        TODO("Not yet implemented")
    }

    private suspend fun processSendGroupMsgAction(params: OneBotV11Action.SendGroupMsg, echo: JsonElement?): OneBotV11ActionResp {
        TODO("Not yet implemented")
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
            return processOneBotAction(
                action.rawAction, action.params, action.echo
            ) ?: OneBotV11ActionResp(
                status = IOneBotActionResp.Status.failed,
                retcode = OneBotV11ActionResp.RetCode.NotFound,
                echo = action.echo,
            )
        } catch (e1: Throwable) {
            when (e1) {
                is QQCodeMessageDataDto -> log.warn { "send request to qq openapi failed (code: ${e1.code}): ${e1.message}" }
                else -> log.warn(e1) { "action process error: ${e1.message}" }
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

    private fun parseAsV11Message(
        content: String,
        attachments: List<MessageAttachment>,
        messageReference: MessageReference?,
    ): CQCodeV11Message? {
        var message: CQCodeV11Message? = null
        messageReference?.let {
            message += CQCodeV11MessageItem.Reply(
                it.messageId
            )
        }
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