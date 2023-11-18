package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.connection.ReverseWebSocketConnection
import kotlinx.serialization.Serializable

object OneBotV11Connection {
    @Serializable
    data class ReverseWebSocket(
        override val url: String? = null,
        val apiUrl: String? = null,
        val eventUrl: String? = null,
        val reconnectInterval: Int = 3000,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            return ReverseWebSocketConnection(url, apiUrl, eventUrl, reconnectInterval)
        }
    }

    @Serializable
    data class HttpPost(
        override val url: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class WebSocket(
        override val url: String,
        val apiUrl: String? = null,
        val eventUrl: String? = null,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    data class Http(
        override val url: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }
}
