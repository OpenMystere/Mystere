package io.github.mystere.core

import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalStdlibApi::class)
interface IMystereBot<EventT: Any>: AutoCloseable {
    val log: KLogger
    val botId: String

    suspend fun connect()
    suspend fun disconnect()

    override fun close() = runBlocking {
        disconnect()
    }

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}


