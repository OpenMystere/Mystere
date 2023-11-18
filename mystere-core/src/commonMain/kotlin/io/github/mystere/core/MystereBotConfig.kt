package io.github.mystere.core

import io.github.mystere.onebot.IOneBotConnection


interface IMystereBot {
    val botId: String

    fun connect(connection: IOneBotConnection)
    fun disconnect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


