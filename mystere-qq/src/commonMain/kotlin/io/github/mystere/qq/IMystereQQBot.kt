package io.github.mystere.qq

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.onebot.v11.connection.applySelfIdHeader
import io.github.mystere.onebot.v12.connection.IOneBotV12Connection
import io.github.mystere.qq.v11.MystereV11QQBot
import io.github.mystere.qq.v12.MystereV12QQBot
import io.github.mystere.qqsdk.QQBot
import io.github.mystere.qqsdk.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qqsdk.qqapi.http.IQQBotAPI
import io.github.mystere.qqsdk.qqapi.http.QQAuthAPI
import io.github.mystere.qqsdk.qqapi.http.QQBotAPI
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketConnection
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.JsonElement
import kotlin.math.max

abstract class IMystereQQBot<ActionT: IOneBotAction, EventT: IOneBotEvent>(
    protected val config: QQBot.Config,
    protected val connectionConfig: IOneBotConnection.IConfig<ActionT>,
): IMystereBot<QQBotWebsocketPayload> {
    override val botId: String = config.appId
    protected abstract val log: KLogger
    private val coroutineScope: CoroutineScope by lazyMystereScope()

    private val mQQBot: QQBot by lazy { QQBot.create(config, EventChannel) }
    protected val QQBotApi: IQQBotAPI get() = mQQBot.BotAPI
    override fun connect() {
        coroutineScope.launch(Dispatchers.IO) {
            OneBotConnection.connect {
                applySelfIdHeader(botId)
                configureOneBotConnection()
            }
            mQQBot.connect()
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
    protected suspend fun sendOneBotEvent(event: EventT) {
        OneBotConnection.onReceiveEvent(event.encodeToJsonElement())
    }
    protected open suspend fun EventT.encodeToJsonElement(): JsonElement {
        throw NotImplementedError("Please implement this extension method in your instance of IMystereQQBot: " +
                "\"fun EventT.encodeToJsonElement(): JsonElement\"")
    }

    private val OneBotActionChannel: Channel<ActionT> = Channel()
    private val OneBotConnection: IOneBotConnection<ActionT> by lazy {
        connectionConfig.createConnection(botId, OneBotActionChannel)
    }
    protected open fun HttpClientConfig<*>.configureOneBotConnection() { }
    protected abstract suspend fun processOneBotAction(action: ActionT)

    override fun disconnect() {
        mQQBot.disconnect()
        coroutineScope.cancel()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is IMystereQQBot<*, *>) other.botId == botId else false
    }

    override fun hashCode(): Int {
        return botId.hashCode()
    }

    companion object {
        fun create(
            config: QQBot.Config,
            connection: IOneBotConnection.IConfig<*>,
        ): IMystereQQBot<*, *> {
            return when (connection) {
                is IOneBotV11Connection.IConfig -> MystereV11QQBot(config, connection)
                is IOneBotV12Connection.IConfig -> MystereV12QQBot(config, connection)
                else -> throw UnsupportedOperationException("Unsupported connection type: ${config::class}")
            }
        }
    }
}