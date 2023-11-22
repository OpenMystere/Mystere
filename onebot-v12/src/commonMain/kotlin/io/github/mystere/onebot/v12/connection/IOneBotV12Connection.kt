package io.github.mystere.onebot.v12.connection

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

abstract class IOneBotV12Connection internal constructor(
    originConfig: IConfig,
    ownBotId: String,
    actionChannel: Channel<IOneBotAction>,
): IOneBotConnection(originConfig, ownBotId, actionChannel) {
    interface IConfig: IOneBotConnection.IConfig

    @Serializable
    data class ReverseWebSocket(
        @SerialName("url")
        override val url: String,
        @SerialName("access-token")
        val accessToken: String,
        @SerialName("reconnect-interval")
        val reconnectInterval: Long = 3000,
    ) : IConfig {
        override fun createConnection(ownBotId: String, actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            return ReverseWebSocketConnection(this, ownBotId, actionChannel)
        }
    }

    @Serializable
    data class HttpWebhook(
        @SerialName("url")
        override val url: String,
    ) : IConfig {
        override fun createConnection(ownBotId: String, actionChannel: Channel<IOneBotAction>): IOneBotConnection {
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
        override fun createConnection(ownBotId: String, actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class Http(
        @SerialName("url")
        override val url: String
    ) : IConfig {
        override fun createConnection(ownBotId: String, actionChannel: Channel<IOneBotAction>): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }
}
