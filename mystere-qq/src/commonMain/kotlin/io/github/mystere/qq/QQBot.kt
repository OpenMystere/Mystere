package io.github.mystere.qq

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.MystereCore
import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.*
import io.github.mystere.onebot.v11.connection.applySelfIdHeader
import io.github.mystere.onebot.v12.IOneBotV12Connection
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.http.QQAuthAPI
import io.github.mystere.qq.qqapi.http.QQBotAPI
import io.github.mystere.qq.qqapi.http.channelsMessage
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketConnection
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qq.qqapi.websocket.message.OpCode0
import io.github.mystere.qq.qqapi.websocket.withData
import io.github.mystere.serialization.cqcode.*
import io.github.mystere.util.withLogging
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

data class QQBot internal constructor(
    private val config: Config,
    override val connectionConfig: IOneBotConnection.IConfig,
): IMystereBot {
    override val botId: String = config.appId

    private val log = KotlinLogging.logger("QQBot(id: ${config.appId})")

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1

    private val coroutineScope: CoroutineScope by lazyMystereScope()

    private var websocket: QQBotWebsocketConnection? = null

    private val QQAuthAPI by lazy {
        QQAuthAPI(
            logger = log,
        )
    }
    private val QQBotAPI by lazy {
        QQBotAPI(
            logger = log,
            appId = config.appId,
            accessTokenProvider = {
                accessToken
            }
        )
    }

    private val QQPayloadChannel: Channel<QQBotWebsocketPayload> = Channel()

    private val OneBotActionChannel: Channel<IOneBotAction> = Channel()
    
    private val connection = connectionConfig.createConnection(OneBotActionChannel)

    override fun connect() {
        coroutineScope.launch(Dispatchers.IO) {
            connection.connect {
                applySelfIdHeader(botId)
                withLogging(log, MystereCore.Debug)
            }
            // 心跳
            delay(5_000)
            while (true) {
                when (connection) {
                    is IOneBotV11Connection -> {
                        connection.onReceiveEvent(MetaHeartbeat(
                            botId, HeartbeatStatus(
                                online = true, good = true
                            )
                        ), MetaHeartbeat.serializer())
                    }
                    is IOneBotV12Connection -> {
                        // TODO
                    }
                }
                delay(30_000)
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                if (accessTokenExpire >= 0) {
                    delay(max(accessTokenExpire - 55, accessTokenExpire) * 1000L)
                    accessTokenExpire = -1
                    continue
                }
                try {
                    QQAuthAPI.getAppAccessToken(AppAccessTokenReqDto(
                        config.appId, config.clientSecret
                    )).let {
                        accessToken = it.accessToken
                        accessTokenExpire = it.expiresIn
                    }
                    log.info { "Token refreshed!" }
                } catch (e: Exception) {
                    log.warn(e) { "Failed to refresh token, retry in 10s..." }
                    accessTokenExpire = 10
                    return@launch
                }

                if (websocket == null) {
                    websocket = QQBotWebsocketConnection(
                        log = log,
                        url = QQBotAPI.gateway().url,
                        channel = QQPayloadChannel,
                    ) provider@{
                        return@provider accessToken
                    }
                }
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                val message = QQPayloadChannel.receive()
                try {
                    when (message.opCode) {
                        QQBotWebsocketPayload.OpCode.Dispatch -> when (message.type) {
                            "AT_MESSAGE_CREATE" -> message.withData<OpCode0.AtMessageCreate> {
                                when (connection) {
                                    is IOneBotV11Connection -> {
                                        val cqMsg: CQCodeMessage = with(this) {
                                            var message: CQCodeMessage? = null
                                            for (item in content) {
                                                if (message == null) {
                                                    message = item.asMessage()
                                                } else {
                                                    message += item
                                                }
                                            }
                                            for (attachment in attachments) {
                                                if (attachment.contentType.startsWith("image")) {
                                                    message = message.plus(CQCodeMessageItem.Image(
                                                        file = attachment.url,
                                                        url = attachment.url,
                                                    ))
                                                }
                                            }
                                            return@with message!!
                                        }
                                        connection.onReceiveEvent(Message(
                                            selfId = config.appId,
                                            messageType = MessageType.guild,
                                            subType = MessageSubType.channel,
                                            messageId = id,
                                            message = cqMsg,
                                            rawMessage = CQCode.encodeToString(cqMsg),
                                            // TODO: 官方貌似不给这个参数
                                            font = 0,
                                            sender = Message.Sender(
                                                userId = author.id
                                            ),
                                            guildId = guildId,
                                            channelId = channelId,
                                        ), Message.serializer())
                                        return@launch
                                    }
                                }
                            }
                        }
                        else -> { }
                    }
                    log.warn { "skip process qq event: ${message.type} (id: ${message.id}, opcode: ${message.opCode})!" }
                } catch (e: Exception) {
                    log.error(e) { "processing qq event error: ${message.type} (id: ${message.id}, opcode: ${message.opCode})!" }
                }
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                val action = OneBotActionChannel.receive()
                try {
                    when (action) {
                        is IOneBotV11Action<*> -> when (val params = action.params) {
                            is SendGuildChannelMsg -> {
                                QQBotAPI.channelsMessage(
                                    channelId = params.channelId,
                                    content = CQCodeMessageItem.Text("阿巴阿巴").asMessage()
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    log.error(e) { "processing onebot action error: ${action::class}!" }
                }
            }
        }
    }

    override fun sendAction(action: IOneBotAction) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        coroutineScope.cancel()
    }

    @Serializable
    data class Config internal constructor(
        @SerialName("app-id")
        val appId: String,
        @SerialName("client-secret")
        val clientSecret: String,
    )

    companion object {
        fun create(
            connection: IOneBotConnection.IConfig,
            config: Config.() -> Unit,
        ): QQBot {
            return QQBot(
                Config("", "").also(config).copy().also {
                    if (it.appId.isBlank()) {
                        throw IllegalArgumentException("empty appId of a QQBot")
                    }
                    if (it.clientSecret.isBlank()) {
                        throw IllegalArgumentException("empty clientSecret of a QQBot")
                    }
                },
                connection,
            )
        }
        fun create(
            config: Config,
            connection: IOneBotConnection.IConfig,
        ): QQBot {
            return QQBot(config, connection)
        }
    }
}
