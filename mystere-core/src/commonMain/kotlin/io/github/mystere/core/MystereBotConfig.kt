package io.github.mystere.core


interface IMystereBot {
    fun connect()
    fun disconnect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


