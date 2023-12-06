package io.github.mystere.core

import io.ktor.client.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

abstract class IMystereBotConnection<ActionT: Any, EventT: Any> private constructor(
    val ownBotId: String,
    protected val eventChannel: Channel<EventT>,
    protected val actionChannel: Channel<ActionT>,
    open val originConfig: IConfig<ActionT>? = null,
): ReceiveChannel<ActionT> by actionChannel, SendChannel<EventT> by eventChannel {
    protected constructor(
        ownBotId: String,
        originConfig: IConfig<ActionT>? = null,
    ): this(ownBotId, Channel(), Channel(), originConfig)

    abstract suspend fun connect()
    abstract suspend fun disconnect()

    interface IConfig<ActionT: Any> {
        fun createConnection(
            ownBotId: String,
        ): IMystereBotConnection<ActionT, out Any>
    }
}
