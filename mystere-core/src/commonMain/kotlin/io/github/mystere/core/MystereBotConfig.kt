package io.github.mystere.core


interface IMystereBot {
    val botId: String

    fun connect()
    fun disconnect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


