package io.github.mystere.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

abstract class IMystereBotConnection<ActionT: Any, EventT: Any, RespT: Any> private constructor(
    val ownBotId: String,
    private val _eventChannel: Channel<EventT>,
    private val _actionChannel: Channel<Pair<ActionT, CompletableDeferred<RespT>>>,
    open val originConfig: IConfig<ActionT>? = null,
): SendChannel<EventT> by _eventChannel, ReceiveChannel<Pair<ActionT, CompletableDeferred<RespT>>> by _actionChannel {
    protected val coroutineScope: CoroutineScope by lazyMystereScope()

    protected val eventChannel: ReceiveChannel<EventT> = _eventChannel
    protected val actionChannel: SendChannel<Pair<ActionT, CompletableDeferred<RespT>>> = _actionChannel

    protected constructor(
        ownBotId: String,
        originConfig: IConfig<ActionT>? = null,
    ): this(ownBotId, Channel(), Channel(), originConfig)

    abstract suspend fun connect()
    abstract suspend fun disconnect()

    interface IConfig<ActionT: Any> {
        fun createConnection(
            ownBotId: String,
        ): IMystereBotConnection<ActionT, out Any, out Any>
    }
}
