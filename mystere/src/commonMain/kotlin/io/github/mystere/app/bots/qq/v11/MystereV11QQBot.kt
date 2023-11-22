package io.github.mystere.app.bots.qq.v11

import io.github.mystere.app.bots.qq.IMystereQQBot
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketPayload
import kotlinx.coroutines.channels.Channel

class MystereV11QQBot(
    override val botId: String
): IMystereQQBot {
    override val EventChannel: Channel<QQBotWebsocketPayload>
        get() = TODO("Not yet implemented")

    override fun connect() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }
}