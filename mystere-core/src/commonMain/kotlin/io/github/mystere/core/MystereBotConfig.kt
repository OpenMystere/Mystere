package io.github.mystere.core


@OptIn(ExperimentalStdlibApi::class)
interface MystereBot: AutoCloseable {
    suspend fun connect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}
