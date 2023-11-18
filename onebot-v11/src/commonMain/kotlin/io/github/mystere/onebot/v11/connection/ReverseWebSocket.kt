package io.github.mystere.onebot.v11.connection

import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.IOneBotV11Connection
import io.github.mystere.onebot.v11.OneBotV11Connection
import io.github.mystere.util.WebsocketClient
import io.ktor.client.*
import io.ktor.client.plugins.api.*

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

import io.ktor.http.*
import kotlinx.coroutines.cancel


fun HttpClientConfig<*>.applySelfIdHeader(selfId: String) {
    install(createClientPlugin("OneBotV11-SelfIdHeader") {
        onRequest { request, _ ->
            request.header("X-Self-ID", selfId)
        }
    })
}

internal class ReverseWebSocketConnection(
    override val originConfig: OneBotV11Connection.ReverseWebSocket,
): IOneBotV11Connection(originConfig) {
    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var _UniWebsocket: DefaultClientWebSocketSession? = null
    private val UniWebsocket: DefaultClientWebSocketSession get() = _UniWebsocket!!

    private var _ApiWebsocket: DefaultClientWebSocketSession? = null
    private val ApiWebsocket: DefaultClientWebSocketSession get() = _ApiWebsocket!!
    private var _EventWebsocket: DefaultClientWebSocketSession? = null
    private val EventWebsocket: DefaultClientWebSocketSession get() = _EventWebsocket!!

    override suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit) {
        _WebsocketClient = WebsocketClient().config(httpClient)
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
    }

    override suspend fun sendEvent(event: IOneBotEvent) {

    }
}