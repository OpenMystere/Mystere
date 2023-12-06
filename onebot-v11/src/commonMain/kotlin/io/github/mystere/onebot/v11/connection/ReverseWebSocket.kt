package io.github.mystere.onebot.v11.connection

import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.UniWebsocketClient
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.api.*

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement


fun HttpClientConfig<*>.applySelfIdHeader(selfId: String) {
    install(createClientPlugin("OneBotV11-SelfIdHeader") {
        onRequest { request, _ ->
            request.header("X-Self-ID", selfId)
        }
    })
}

internal class ReverseWebSocketConnection(
    ownBotId: String,
    override val originConfig: ReverseWebSocket,
): IOneBotV11Connection(ownBotId, originConfig) {
    private val log = KotlinLogging.logger("OneBotV11-ReverseWebSocketConnection(ownBotId: $ownBotId)")

    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var _UniWebsocket: DefaultClientWebSocketSession? = null
    private var _ApiWebsocket: DefaultClientWebSocketSession? = null
    private val ApiWebsocket: DefaultClientWebSocketSession get() = (_ApiWebsocket ?: _UniWebsocket)!!
    private var _EventWebsocket: DefaultClientWebSocketSession? = null
    private val EventWebsocket: DefaultClientWebSocketSession get() = (_EventWebsocket ?: _UniWebsocket)!!

    override suspend fun connect() {
        _WebsocketClient = UniWebsocketClient().config {
            applySelfIdHeader(ownBotId)
        }
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (originConfig.url != null) {
                        _UniWebsocket?.cancel()
                        _UniWebsocket = WebsocketClient.webSocketSession {
                            this.url.takeFrom(Url(originConfig.url))
                        }
                    } else if (originConfig.apiUrl != null && originConfig.eventUrl != null) {
                        _ApiWebsocket?.cancel()
                        _ApiWebsocket = WebsocketClient.webSocketSession {
                            this.url.takeFrom(Url(originConfig.apiUrl))
                        }
                        _EventWebsocket?.cancel()
                        _EventWebsocket = WebsocketClient.webSocketSession {
                            this.url.takeFrom(Url(originConfig.eventUrl))
                        }
                    } else {
                        throw OneBotConnectionException("url not set or apiUrl and eventUrl both not set!")
                    }
                    while (true) {
                        try {
                            log.debug { "waiting for new onebot action" }
                            val action = MystereJson.decodeFromJsonElement<OneBotV11Action>(
                                ApiWebsocket.receiveDeserialized<JsonElement>()
                            )
                            log.debug { "new onebot action! action: ${action.rawAction}" }
                            coroutineScope.launch(Dispatchers.IO) {
                                val result = CompletableDeferred<OneBotV11ActionResp>()
                                actionChannel.send(action to result)
                                try {
                                    val resp = withTimeout(60_000) {
                                        result.await()
                                    }
                                    log.info { "send response body: ${resp::class}" }
                                    val rawBody = MystereJson.encodeToString(resp)
                                    log.debug { "send response body: $rawBody" }
                                    ApiWebsocket.send(Frame.Text(rawBody))
                                } catch (e: Throwable) {
                                    if (e is TimeoutCancellationException) {
                                        log.warn(e) { "action request timeout!" }
                                    } else {
                                        log.warn(e) { "response body send error" }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            log.warn(e) { "error during sending action" }
                        }
                    }
                } catch (e: Throwable) {
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
                    EventWebsocket.send(Frame.Text(rawEvent))
                } catch (e: Exception) {
                    log.warn(e) { "event send error" }
                }
            }
        }
    }

    override suspend fun disconnect() {
        _UniWebsocket?.cancel()
        _EventWebsocket?.cancel()
        _ApiWebsocket?.cancel()
    }
}