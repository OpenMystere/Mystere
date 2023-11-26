package io.github.mystere.onebot.v11.connection

import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.IOneBotV11Event
import io.github.mystere.onebot.v11.OneBotV11Action
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

abstract class IOneBotV11Connection internal constructor(
    ownBotId: String,
    originConfig: IConfig,
): IOneBotConnection<OneBotV11Action, IOneBotV11Event>(
    ownBotId, originConfig,
) {
    interface IConfig: IOneBotConnection.IConfig<OneBotV11Action> {
        override fun createConnection(
            ownBotId: String,
        ): IOneBotConnection<OneBotV11Action, IOneBotV11Event>
    }

    @Serializable
    data class ReverseWebSocket(
        @SerialName("url")
        override val url: String? = null,
        @SerialName("api-url")
        val apiUrl: String? = null,
        @SerialName("event-url")
        val eventUrl: String? = null,
        @SerialName("reconnect-interval")
        val reconnectInterval: Long = 3000,
        @SerialName("heartbeat")
        val heartbeat: Int = 20_000,
    ) : IConfig {
        override fun createConnection(
            ownBotId: String
        ): IOneBotV11Connection {
            return ReverseWebSocketConnection(ownBotId, this)
        }
    }

    @Serializable
    data class HttpPost(
        @SerialName("url")
        override val url: String,
    ) : IConfig {
        override fun createConnection(
            ownBotId: String
        ): IOneBotV11Connection {
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
    ) : IConfig {
        override fun createConnection(
            ownBotId: String
        ): IOneBotV11Connection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class Http(
        @SerialName("url")
        override val url: String
    ) : IConfig {
        override fun createConnection(
            ownBotId: String
        ): IOneBotV11Connection {
            TODO("Not yet implemented")
        }
    }
}
