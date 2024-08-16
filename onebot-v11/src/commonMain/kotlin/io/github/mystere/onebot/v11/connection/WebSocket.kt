package io.github.mystere.onebot.v11.connection

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.OneBotConnectionException
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.*

internal class WebSocketConnection(
    override val originConfig: WebSocket,
): IOneBotV11Connection(originConfig) {
    private val log: KLogger by lazy {
        KotlinLogging.logger("OneBotV11-WebSocketConnection(ownBotId: $ownBotId)")
    }

    private var ApiConnection: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    private var EventConnection: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    override suspend fun connect(ownBotId: String) {
        super.connect(ownBotId)
        coroutineScope.launch(Dispatchers.IO) {
            if (originConfig.url != null) {
                val urlListen = Url(originConfig.url)
                ApiConnection = coroutineScope.embeddedServer(
                    factory = CIO,
                    port = urlListen.port,
                    host = urlListen.host,
                ) {
                    install(WebSockets) {
                        contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
                    }
                }
                ApiConnection?.start(true)
                EventConnection = ApiConnection
            } else if (originConfig.apiUrl != null && originConfig.eventUrl != null) {
                val apiListen = Url(originConfig.apiUrl)
                ApiConnection = coroutineScope.embeddedServer(
                    factory = CIO,
                    port = apiListen.port,
                    host = apiListen.host,
                ) {
                    install(WebSockets) {
                        contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
                    }
                }
                ApiConnection?.start(true)

                val eventListen = Url(originConfig.eventUrl)
                EventConnection = coroutineScope.embeddedServer(
                    factory = CIO,
                    port = eventListen.port,
                    host = eventListen.host,
                ) {
                    install(WebSockets) {
                        contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
                    }
                }
                EventConnection?.start(true)
            } else {
                throw OneBotConnectionException("url not set or apiUrl and eventUrl both not set!")
            }
            val childScope = CoroutineScope(coroutineScope.coroutineContext + Job())
            while (true) {

            }
        }
    }

    override suspend fun disconnect() {
        ApiConnection?.stop()
        EventConnection?.stop()
    }
}