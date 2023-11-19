package io.github.mystere.core

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection


interface IMystereBot {
    val botId: String
    val connectionConfig: IOneBotConnection.IConfig

    fun connect()
    fun disconnect()

    fun sendAction(action: IOneBotAction)

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


