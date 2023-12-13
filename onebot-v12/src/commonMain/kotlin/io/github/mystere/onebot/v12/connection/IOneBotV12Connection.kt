package io.github.mystere.onebot.v12.connection

import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v12.OneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import io.github.mystere.onebot.v12.OneBotV12ActionResp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

abstract class IOneBotV12Connection internal constructor(
    originConfig: IConfig,
): IOneBotConnection<OneBotV12Action, OneBotV12Event, OneBotV12ActionResp>(originConfig) {
    final override val versionName: String = "12"

    interface IConfig: IOneBotConnection.IConfig<OneBotV12Action> {
        override fun createConnection(
            ownBotId: String,
        ): IOneBotConnection<OneBotV12Action, OneBotV12Event, OneBotV12ActionResp>
    }

    @Serializable
    data class ReverseWebSocket(
        @SerialName("url")
        override val url: String,
        @SerialName("access-token")
        val accessToken: String,
        @SerialName("reconnect-interval")
        val reconnectInterval: Long = 3000,
    ) : IConfig {
        override fun createConnection(
            ownBotId: String,
        ): IOneBotV12Connection {
            return ReverseWebSocketConnection(this)
        }
    }

    @Serializable
    data class HttpWebhook(
        @SerialName("url")
        override val url: String,
    ) : IConfig {
        override fun createConnection(
            ownBotId: String
        ): IOneBotV12Connection {
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
        ): IOneBotV12Connection {
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
        ): IOneBotV12Connection {
            TODO("Not yet implemented")
        }
    }
}
