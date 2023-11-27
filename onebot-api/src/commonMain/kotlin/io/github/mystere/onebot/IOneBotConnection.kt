package io.github.mystere.onebot

import io.github.mystere.core.IMystereBotConnection

abstract class IOneBotConnection<ActionT: IOneBotAction, EventT: IOneBotEvent> protected constructor(
    ownBotId: String,
    override val originConfig: IConfig<ActionT>
): IMystereBotConnection<ActionT, EventT>(
    ownBotId, originConfig
) {
    interface IConfig<ActionT: IOneBotAction>: IMystereBotConnection.IConfig<ActionT> {
        val url: String?

        override fun createConnection(
            ownBotId: String,
        ): IOneBotConnection<ActionT, out IOneBotEvent>
    }
}

interface IOneBotAction {
    val action: Action
    interface Param
    interface Action
}
interface IOneBotActionResp {
    enum class Status {
        ok, failed,
    }
    interface Data
    interface RetCode {
        val rawCode: Long
    }
}

interface IOneBotEvent
