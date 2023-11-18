package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotConnection
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

internal abstract class IOneBotV11Connection: IOneBotConnection

internal suspend fun HttpClient.WebsocketConnection(url: String): OneBotClientWebSocketSession {
    return OneBotClientWebSocketSession(
        webSocketSession {
            this.url.takeFrom(Url(url))
        }
    )
}

class OneBotClientWebSocketSession(
    private val delegate: DefaultClientWebSocketSession
): DefaultWebSocketSession by delegate {
    @OptIn(InternalAPI::class)
    override fun start(negotiatedExtensions: List<WebSocketExtension<*>>) {
        delegate.start(negotiatedExtensions)

        // 心跳
        launch(Dispatchers.IO) {
            while (isActive) {

            }
        }
    }
}
