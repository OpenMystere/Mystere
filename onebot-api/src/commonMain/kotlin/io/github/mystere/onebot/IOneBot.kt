package io.github.mystere.onebot

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.lazyMystereScope

abstract class IOneBot<EventT: IOneBotEvent, ActionT: IOneBotAction> protected constructor(
    override val botId: String,
    protected val OneBotConnection: IOneBotConnection<ActionT, EventT>,
): IMystereBot<EventT> {
    protected val coroutineScope by lazyMystereScope()

    override suspend fun connect() {
        OneBotConnection.connect()
    }
}