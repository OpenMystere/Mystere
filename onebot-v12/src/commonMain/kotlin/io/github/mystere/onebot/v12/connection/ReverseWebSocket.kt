package io.github.mystere.onebot.v12.connection

import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.UniWebsocketClient
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.OneBotV12ActionResp
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString


internal class ReverseWebSocketConnection(
    ownBotId: String,
    override val originConfig: ReverseWebSocket,
): IOneBotV12Connection(ownBotId, originConfig) {
    private val log = KotlinLogging.logger("OneBotV12Connection(ownBotId: $ownBotId)")

    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var UniWebsocket: DefaultClientWebSocketSession? = null

    override suspend fun connect() {
        _WebsocketClient = UniWebsocketClient()
        coroutineScope.launch(Dispatchers.IO) {
            val childScope = CoroutineScope(coroutineScope.coroutineContext + Job())
            while (true) {
                try {
                    UniWebsocket?.cancel()
                    UniWebsocket = WebsocketClient.webSocketSession {
                        this.url.takeFrom(Url(originConfig.url))
                    }
                    while (true) {
                        log.debug { "waiting for new onebot action" }
                        val action = UniWebsocket!!.receiveDeserialized<OneBotV12Action>()
                        log.debug { "new onebot action! action: ${action.action}" }
                        childScope.launch(Dispatchers.IO) {
                            val result = CompletableDeferred<OneBotV12ActionResp>()
                            actionChannel.send(action to result)
                            try {
                                val resp = withTimeout(60_000) {
                                    result.await()
                                }
                                log.info { "send response body: ${resp::class}" }
                                val rawBody = MystereJson.encodeToString(resp)
                                log.debug { "send response body: $rawBody" }
                                UniWebsocket?.send(Frame.Text(rawBody))
                            } catch (e: Throwable) {
                                if (e is TimeoutCancellationException) {
                                    log.warn(e) { "action request timeout!" }
                                } else {
                                    log.warn(e) { "response body send error" }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    log.warn(e) { "WebSocket disconnected, reconnect in ${originConfig.reconnectInterval}ms..." }
                }
                delay(originConfig.reconnectInterval)
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            for (event in eventChannel) {
                try {
                    log.info { "receive event: ${event::class}" }
                    val rawEvent = MystereJson.encodeToString(event)
                    log.debug { "receive event: $rawEvent" }
                    UniWebsocket?.send(Frame.Text(rawEvent))
                } catch (e: Exception) {
                    log.warn(e) { "event send error" }
                }
            }
        }
    }

    override suspend fun disconnect() {
        UniWebsocket?.cancel()
    }
}