package io.github.mystere.onebot.v11.connection

import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.OneBotV11Action
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.UniWebsocketClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.api.*

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement


fun HttpClientConfig<*>.applySelfIdHeader(selfId: String) {
    install(createClientPlugin("OneBotV11-SelfIdHeader") {
        onRequest { request, _ ->
            request.header("X-Self-ID", selfId)
        }
    })
}

internal class ReverseWebSocketConnection(
    override val originConfig: ReverseWebSocket,
    ownBotId: String,
    actionChannel: Channel<OneBotV11Action>,
): IOneBotV11Connection(originConfig, ownBotId, actionChannel) {
    private val log = KotlinLogging.logger("OneBotV11Connection(ownBotId: $ownBotId)")

    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var _UniWebsocket: DefaultClientWebSocketSession? = null
    private var _ApiWebsocket: DefaultClientWebSocketSession? = null
    private val ApiWebsocket: DefaultClientWebSocketSession get() = (_ApiWebsocket ?: _UniWebsocket)!!
    private var _EventWebsocket: DefaultClientWebSocketSession? = null
    private val EventWebsocket: DefaultClientWebSocketSession get() = (_EventWebsocket ?: _UniWebsocket)!!

    private val coroutineScope by lazyMystereScope()

    override suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit) {
        _WebsocketClient = UniWebsocketClient().config(httpClient)
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
                        log.debug { "waiting for new onebot action" }
                        val action = ApiWebsocket.receiveDeserialized<OneBotV11Action>()
                        log.debug { "new onebot action! action: ${action.action}" }
                        try {
                            actionChannel.send(action)
                        } catch (e: Exception) {
                            log.warn(e) { "error during sending action ${action.action}" }
                        }
                    }
                } catch (e: Exception) {
                    log.warn(e) { "WebSocket disconnected, reconnect in ${originConfig.reconnectInterval}ms..." }
                }
                delay(originConfig.reconnectInterval)
            }
        }
    }

    override suspend fun onReceiveEvent(event: JsonElement) {
        log.info { "receive event: ${event::class}" }
        val rawEvent = MystereJson.encodeToString(event)
        log.debug { "receive event: $rawEvent" }
        EventWebsocket.send(Frame.Text(rawEvent))
    }
}