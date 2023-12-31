package io.github.mystere.qqsdk.qqapi.websocket.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object OpCode10 {
    @Serializable
    data class QQHelloMessage(
        @SerialName("heartbeat_interval")
        val heartbeatInterval: Long,
    ): OpCodeData
}