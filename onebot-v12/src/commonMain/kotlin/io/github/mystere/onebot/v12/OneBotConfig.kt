package io.github.mystere.onebot.v12

import io.github.mystere.onebot.OneBotConnection

object OneBotV12Connection {
    data class ReverseWebSocket(
        override val url: String,
    ): OneBotConnection
}
