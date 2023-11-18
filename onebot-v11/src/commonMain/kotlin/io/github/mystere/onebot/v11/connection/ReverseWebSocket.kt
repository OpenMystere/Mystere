package io.github.mystere.onebot.v11.connection

import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.IOneBotV11Connection
import io.github.mystere.onebot.v11.OneBotClientWebSocketSession
import io.github.mystere.onebot.v11.WebsocketConnection
import io.github.mystere.util.WebsocketClient
import io.ktor.client.*

internal class ReverseWebSocketConnection(
    private val url: String? = null,
    private val apiUrl: String? = null,
    private val eventUrl: String? = null,
    private val reconnectInterval: Int = 3000,
): IOneBotV11Connection() {
    private val WebsocketClient: HttpClient by lazy { WebsocketClient() }

    private var _UniWebsocket: OneBotClientWebSocketSession? = null
    private val UniWebsocket: OneBotClientWebSocketSession get() = _UniWebsocket!!

    private var _ApiWebsocket: OneBotClientWebSocketSession? = null
    private val ApiWebsocket: OneBotClientWebSocketSession get() = _ApiWebsocket!!
    private var _EventWebsocket: OneBotClientWebSocketSession? = null
    private val EventWebsocket: OneBotClientWebSocketSession get() = _EventWebsocket!!

    override suspend fun init() {
        if (url != null) {
            _UniWebsocket = WebsocketClient.WebsocketConnection(url)
        } else if (apiUrl != null && eventUrl != null) {
            _ApiWebsocket = WebsocketClient.WebsocketConnection(apiUrl)
            _EventWebsocket = WebsocketClient.WebsocketConnection(eventUrl)
        } else {
            throw OneBotConnectionException("url not set or apiUrl and eventUrl both not set!")
        }
    }

    override suspend fun sendEvent(event: IOneBotEvent) {

    }
}