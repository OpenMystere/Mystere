package io.github.mystere.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.Serializable

abstract class IMystereBotConnection<ActionT: Any, EventT: Any, RespT: Any> private constructor(
    private val _eventChannel: Channel<EventT>,
    private val _actionChannel: Channel<Pair<ActionT, CompletableDeferred<RespT>>>,
    open val originConfig: IConfig<ActionT>? = null,
): SendChannel<EventT> by _eventChannel, ReceiveChannel<Pair<ActionT, CompletableDeferred<RespT>>> by _actionChannel {
    protected val coroutineScope: CoroutineScope by lazyMystereScope()

    protected val eventChannel: ReceiveChannel<EventT> = _eventChannel
    protected val actionChannel: SendChannel<Pair<ActionT, CompletableDeferred<RespT>>> = _actionChannel

    protected constructor(
        originConfig: IConfig<ActionT>? = null,
    ): this(Channel(), Channel(), originConfig)

    private var _ownBotId: String? = null
    val ownBotId: String get() = _ownBotId ?: throw IllegalStateException("Connection has not been connected!")
    open suspend fun connect(ownBotId: String) {
        _ownBotId = ownBotId
    }
    abstract suspend fun disconnect()

    interface IConfig<ActionT: Any> {
        fun createConnection(
            ownBotId: String,
        ): IMystereBotConnection<out Any, out Any, out Any>
    }
}
