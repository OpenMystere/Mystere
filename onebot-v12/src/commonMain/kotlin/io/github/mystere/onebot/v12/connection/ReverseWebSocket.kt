package io.github.mystere.onebot.v12.connection

import io.github.mystere.core.lazyMystereScope
import io.github.mystere.onebot.v12.OneBotV12Action
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
    actionChannel: Channel<OneBotV12Action>,
): IOneBotV12Connection(originConfig, ownBotId, actionChannel) {
    private val log = KotlinLogging.logger("OneBotV12Connection(ownBotId: $ownBotId)")

    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var UniWebsocket: DefaultClientWebSocketSession? = null

    private val coroutineScope by lazyMystereScope()

    override suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit) {
        _WebsocketClient = UniWebsocketClient().config(httpClient)
        coroutineScope.launch(Dispatchers.IO) {
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
        UniWebsocket?.send(Frame.Text(rawEvent))
    }
}