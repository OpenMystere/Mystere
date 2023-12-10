package io.github.mystere.onebot.v11.connection

import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.MystereJsonClassDiscriminator
import io.github.mystere.core.util.UniWebsocketClient
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.mystere.onebot.v11.OneBotV11Event
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.api.*

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*


fun HttpClientConfig<*>.applySelfIdHeader(selfId: String) {
    install(createClientPlugin("OneBotV11-SelfIdHeader") {
        onRequest { request, _ ->
            request.header("X-Self-ID", selfId)
        }
    })
}

internal class ReverseWebSocketConnection(
    override val originConfig: ReverseWebSocket,
): IOneBotV11Connection(originConfig) {
    private val log: KLogger by lazy {
        KotlinLogging.logger("OneBotV11-ReverseWebSocketConnection(ownBotId: $ownBotId)")
    }

    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var _UniWebsocket: DefaultClientWebSocketSession? = null
    private var _ApiWebsocket: DefaultClientWebSocketSession? = null
    private val ApiWebsocket: DefaultClientWebSocketSession get() = (_ApiWebsocket ?: _UniWebsocket)!!
    private var _EventWebsocket: DefaultClientWebSocketSession? = null
    private val EventWebsocket: DefaultClientWebSocketSession get() = (_EventWebsocket ?: _UniWebsocket)!!

    override suspend fun connect(ownBotId: String) {
        super.connect(ownBotId)
        _WebsocketClient = UniWebsocketClient().config {
            applySelfIdHeader(ownBotId)
        }
        coroutineScope.launch(Dispatchers.IO) {
            val childScope = CoroutineScope(coroutineScope.coroutineContext + Job())
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
                    log.info { "WebSocket connected!" }
                    while (true) {
                        log.debug { "waiting for new onebot action" }
                        val action = MystereJson.decodeFromJsonElement<OneBotV11Action>(
                            ApiWebsocket.receiveDeserialized<JsonElement>()
                        )
                        try {
                            log.debug { "new onebot action! action: ${action.rawAction}" }
                            childScope.launch(Dispatchers.IO) {
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
            for (event: OneBotV11Event in eventChannel) {
                try {
                    log.info { "receive event: ${event.params::class}" }
                    val rawEvent = MystereJson.encodeToString(event)
                    log.debug { "receive event: $rawEvent" }
                    EventWebsocket.send(Frame.Text(rawEvent))
                } catch (e: Throwable) {
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