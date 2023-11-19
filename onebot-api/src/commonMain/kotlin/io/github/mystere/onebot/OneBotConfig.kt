package io.github.mystere.onebot

import io.ktor.client.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.KSerializer

abstract class IOneBotConnection(
    open val originConfig: IConfig,
    val actionChannel: Channel<IOneBotAction>,
) {
    abstract suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit)
    abstract suspend fun <T: IOneBotEvent> onReceiveEvent(event: T, serializer: KSerializer<T>)


    interface IConfig {
        val url: String?

        fun createConnection(actionChannel: Channel<IOneBotAction>): IOneBotConnection
    }
}

interface IOneBotAction {
    interface Param {
        val action: String
    }
}

interface IOneBotEvent
