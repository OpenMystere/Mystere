package io.github.mystere.app.bots.qq

import io.github.mystere.core.IMystereBot
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.connection.IOneBotV11Connection
import io.github.mystere.qq.QQBot
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketPayload

interface IMystereQQBot: IMystereBot<QQBotWebsocketPayload> {
    companion object {
        fun create(
            config: QQBot.Config,
            connection: IOneBotConnection.IConfig,
        ): IMystereQQBot {
            when (connection) {
                is IOneBotV11Connection.IConfig -> when (connection) {
                    is IOneBotV11Connection.ReverseWebSocket -> {

                    }
                }
            }
            throw UnsupportedOperationException("Unsupported connection type: ${config::class}")
        }
    }
}