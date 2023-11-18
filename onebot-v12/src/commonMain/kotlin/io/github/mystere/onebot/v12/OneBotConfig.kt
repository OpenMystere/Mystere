package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotConnection
import kotlinx.serialization.Serializable

object OneBotV12Connection {
    @Serializable
    data class ReverseWebSocket(
        override val url: String,
        val accessToken: String? = null,
        val reconnectInterval: Int = 3000,
    ) : IOneBotConnection.IConfig

    @Serializable
    data class WebSocket(
        override val url: String,
        val accessToken: String,
    ) : IOneBotConnection.IConfig

    @Serializable
    data class HttpWebhook(
        override val url: String,
        val apiUrl: String?,
        val eventUrl: String?,
    ) : IOneBotConnection.IConfig

    @Serializable
    @Deprecated("不打算真正实现")
    data class Http(
        override val url: String,
        val apiUrl: String?,
        val eventUrl: String?,
    ) : IOneBotConnection.IConfig
}
