package io.github.mystere.onebot

import io.ktor.client.*

abstract class IOneBotConnection(
    open val originConfig: IConfig,
) {
    abstract suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit)
    abstract suspend fun sendEvent(event: IOneBotEvent)


    interface IConfig {
        val url: String?

        fun createConnection(): IOneBotConnection
    }
}

interface IOneBotAction {
    interface Param {
        val action: String
    }
}

interface IOneBotEvent
