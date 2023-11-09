package io.github.mystere.onebot.v11

import io.github.mystere.onebot.OneBotConnection

object OneBotV11Connection {
    data class ReverseWebSocket(
        override val url: String,
        val apiUrl: String?,
        val eventUrl: String?,
    ) : OneBotConnection
}
