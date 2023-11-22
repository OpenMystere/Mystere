package io.github.mystere.qq

import io.github.mystere.qq.v11.MystereV11QQBot
import io.github.mystere.qq.v12.MystereV12QQBot
import io.github.mystere.core.IMystereBot
import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.connection.applySelfIdHeader
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketConnection
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.oshai.kotlinlogging.KLogger
import kotlin.math.max
import io.github.mystere.qqsdk.qqapi.http.QQAuthAPI
import io.github.mystere.qqsdk.qqapi.http.QQBotAPI
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.KSerializer

abstract class IMystereQQBot<ActionT: IOneBotAction>(
    protected val config: QQBot.Config,
    protected val connectionConfig: IOneBotConnection.IConfig<ActionT>,
): IMystereBot<QQBotWebsocketPayload> {
    override val botId: String = config.appId
    protected abstract val log: KLogger
    private val coroutineScope: CoroutineScope by lazyMystereScope()

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
    private var QQBotConnection: QQBotWebsocketConnection? = null

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1
    override fun connect() {
        coroutineScope.launch(Dispatchers.IO) {
            OneBotConnection.connect {
                applySelfIdHeader(botId)
                configureOneBotConnection()
            }
            while (true) {
                if (accessTokenExpire >= 0) {
                    delay(max(accessTokenExpire - 55, accessTokenExpire) * 1000L)
                    accessTokenExpire = -1
                    continue
                }
                try {
                    QQAuthAPI.getAppAccessToken(
                        AppAccessTokenReqDto(
                            config.appId, config.clientSecret
                        )
                    ).let {
                        accessToken = it.accessToken
                        accessTokenExpire = it.expiresIn
                    }
                    log.info { "Token refreshed!" }
                } catch (e: Exception) {
                    log.warn(e) { "Failed to refresh token, retry in 10s..." }
                    accessTokenExpire = 10
                    return@launch
                }

                if (QQBotConnection == null) {
                    QQBotConnection = QQBotWebsocketConnection(
                        log = log,
                        url = QQBotAPI.gateway().url,
                        channel = EventChannel,
                    ) provider@{
                        return@provider accessToken
                    }
                }
            }
        }

        coroutineScope.launch(Dispatchers.IO) {
            for (payload: QQBotWebsocketPayload in EventChannel) {
                try {
                    processQQEvent(payload)
                } catch (e: Exception) {
                    log.warn(e) { "process qq event error" }
                }
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            for (action: ActionT in OneBotActionChannel) {
                try {
                    processOneBotAction(action)
                } catch (e: Exception) {
                    log.warn(e) { "process onebot v11 action error" }
                }
            }
        }
    }

    override val EventChannel: Channel<QQBotWebsocketPayload> = Channel()
    protected abstract suspend fun processQQEvent(event: QQBotWebsocketPayload)
    protected suspend fun <T: IOneBotEvent> sendOneBotEvent(event: T, serializer: KSerializer<T>) {
        OneBotConnection.onReceiveEvent(event, serializer)
    }

    private val OneBotActionChannel: Channel<ActionT> = Channel()
    private val OneBotConnection: IOneBotConnection<ActionT> by lazy {
        connectionConfig.createConnection(botId, OneBotActionChannel)
    }
    protected open fun HttpClientConfig<*>.configureOneBotConnection() { }
    protected abstract suspend fun processOneBotAction(action: ActionT)

    override fun disconnect() {
        QQBotConnection?.close()
        coroutineScope.cancel()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is IMystereQQBot<*>) other.botId == botId else false
    }

    override fun hashCode(): Int {
        return botId.hashCode()
    }

    companion object {
        fun create(
            config: QQBot.Config,
            connection: IOneBotConnection.IConfig<*>,
        ): IMystereQQBot<*> {
            return when (connection) {
                is IOneBotV11Connection.IConfig -> MystereV11QQBot(config, connection)
                is IOneBotV12Connection.IConfig -> MystereV12QQBot(config, connection)
                else -> throw UnsupportedOperationException("Unsupported connection type: ${config::class}")
            }
        }
    }
}