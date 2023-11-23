package io.github.mystere.onebot.v12.connection

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v12.IOneBotV12Event
import io.github.mystere.onebot.v12.OneBotV12Action
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.encodeToJsonElement

abstract class IOneBotV12Connection internal constructor(
    originConfig: IConfig,
    ownBotId: String,
    actionChannel: Channel<OneBotV12Action>,
): IOneBotConnection<OneBotV12Action>(originConfig, ownBotId, actionChannel) {
    suspend fun onReceiveEvent(event: IOneBotV12Event) {
        onReceiveEvent(MystereJson.encodeToJsonElement(event))
    }

    interface IConfig: IOneBotConnection.IConfig<OneBotV12Action> {
        override fun createConnection(
            ownBotId: String,
            actionChannel: Channel<OneBotV12Action>
        ): IOneBotConnection<OneBotV12Action>
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
            actionChannel: Channel<OneBotV12Action>
        ): IOneBotConnection<OneBotV12Action> {
            return ReverseWebSocketConnection(this, ownBotId, actionChannel)
        }
    }

    @Serializable
    data class HttpWebhook(
        @SerialName("url")
        override val url: String,
    ) : IConfig {
        override fun createConnection(
            ownBotId: String, actionChannel: Channel<OneBotV12Action>,
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
            ownBotId: String, actionChannel: Channel<OneBotV12Action>,
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
            ownBotId: String, actionChannel: Channel<OneBotV12Action>,
        ): IOneBotV12Connection {
            TODO("Not yet implemented")
        }
    }
}
