package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotConnection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object OneBotV12Connection {
    @Serializable
    data class ReverseWebSocket(
        @SerialName("url")
        override val url: String,
        @SerialName("access-token")
        val accessToken: String? = null,
        @SerialName("reconnect-interval")
        val reconnectInterval: Int = 3000,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class WebSocket(
        @SerialName("url")
        override val url: String,
        @SerialName("access-token")
        val accessToken: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class HttpWebhook(
        @SerialName("url")
        override val url: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    data class Http(
        @SerialName("url")
        override val url: String,
    ) : IOneBotConnection.IConfig {
        override fun createConnection(): IOneBotConnection {
            TODO("Not yet implemented")
        }
    }
}
