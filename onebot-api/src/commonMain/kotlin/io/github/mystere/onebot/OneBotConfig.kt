package io.github.mystere.onebot

interface IOneBotConnection {
    suspend fun init()
    suspend fun sendEvent(event: IOneBotEvent)


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
