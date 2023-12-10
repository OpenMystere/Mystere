package io.github.mystere.onebot

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.lazyMystereScope
import kotlinx.coroutines.*

abstract class IOneBot<EventT: IOneBotEvent, ActionT: IOneBotAction, RespT: IOneBotActionResp> protected constructor(
    protected val OneBotConnection: IOneBotConnection<ActionT, EventT, RespT>,
): IMystereBot<EventT> {
    abstract override val botId: String
    protected val coroutineScope by lazyMystereScope()

    override suspend fun connect() {
        throw NotImplementedError("Use connect(ownBotId: String) instead.")
    }

    protected suspend fun connect(ownBotId: String) {
        OneBotConnection.connect(ownBotId)

        coroutineScope.launch(Dispatchers.IO) {
            val childScope = CoroutineScope(coroutineScope.coroutineContext + Job())
            for ((action, resp) in OneBotConnection) {
                childScope.launch(Dispatchers.IO) {
                    try {
                        resp.complete(processOneBotAction(action))
                    } catch (e1: Throwable) {
                        try {
                            resp.complete(onProcessInternalError(e1, action))
                        } catch (e2: Throwable) {
                            resp.completeExceptionally(e2)
                        }
                    }
                }
            }
        }
    }

    protected abstract suspend fun processOneBotAction(action: ActionT): RespT

    protected abstract suspend fun onProcessInternalError(e: Throwable, originAction: ActionT): RespT
}