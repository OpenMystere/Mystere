package io.github.mystere.onebot

import io.ktor.client.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.JsonElement

abstract class IOneBotConnection<ChaT: IOneBotAction>(
    open val originConfig: IConfig<ChaT>,
    val ownBotId: String,
    val actionChannel: Channel<ChaT>,
) {
    abstract suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit)
    abstract suspend fun onReceiveEvent(event: JsonElement)


    interface IConfig<ChaT: IOneBotAction> {
        val url: String?

        fun createConnection(
            ownBotId: String,
            actionChannel: Channel<ChaT>,
        ): IOneBotConnection<ChaT>
    }
}

interface IOneBotAction {
    interface Param {
        val action: Action
    }
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
