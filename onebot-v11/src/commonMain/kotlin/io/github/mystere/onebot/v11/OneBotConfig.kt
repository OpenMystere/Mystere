package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.connection.ReverseWebSocketConnection
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object OneBotV11Connection {
    @Serializable
    data class ReverseWebSocket(
        @SerialName("url")
        override val url: String? = null,
        @SerialName("api-url")
        val apiUrl: String? = null,
        @SerialName("event-url")
        val eventUrl: String? = null,
        @SerialName("reconnect-interval")
        val reconnectInterval: Int = 3000,
        @SerialName("heartbeat")
        val heartbeat: Int = 20_000,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            return ReverseWebSocketConnection(this, actionChannel)
        }
    }

    @Serializable
    data class HttpPost(
        @SerialName("url")
        override val url: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class WebSocket(
        @SerialName("url")
        override val url: String? = null,
        @SerialName("api-url")
        val apiUrl: String? = null,
        @SerialName("event-url")
        val eventUrl: String? = null,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class Http(
        @SerialName("url")
        override val url: String
    ) : IOneBotConnection.IConfig {
        override fun createConnection(actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }
}
