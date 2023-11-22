package io.github.mystere.core

import kotlinx.coroutines.channels.Channel


interface IMystereBot<T: Any> {
    val botId: String
    val EventChannel: Channel<T>

    fun connect()
    fun disconnect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


