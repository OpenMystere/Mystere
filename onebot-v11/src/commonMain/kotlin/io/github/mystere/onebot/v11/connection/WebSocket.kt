package io.github.mystere.onebot.v11.connection

import io.github.mystere.core.util.MystereJson
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.OneBotV11ActionResp
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.websocket.*

internal class WebSocketConnection(
    ownBotId: String,
    override val originConfig: WebSocket,
): IOneBotV11Connection(ownBotId, originConfig) {
    private val log = KotlinLogging.logger("OneBotV11-WebSocketConnection(ownBotId: $ownBotId)")

    private var ApiConnection: ApplicationEngine? = null
    private var EventConnection: ApplicationEngine? = null

    private val ApiApplication: Application? get() = ApiConnection?.application
    private val EventApplication: Application? get() = EventConnection?.application

    override suspend fun connect() {
        if (originConfig.url != null) {
            val urlListen = Url(originConfig.url)
            ApiConnection = coroutineScope.embeddedServer(
                factory = CIO,
                port = urlListen.port,
                host = urlListen.host,
                path = urlListen.encodedPath,
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
                path = apiListen.encodedPath,
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
                path = eventListen.encodedPath,
            ) {
                install(WebSockets) {
                    contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
                }
            }
            EventConnection?.start(true)
        } else {
            throw OneBotConnectionException("url not set or apiUrl and eventUrl both not set!")
        }
    }

    override suspend fun disconnect() {
        ApiConnection?.stop()
        EventConnection?.stop()
    }

    override suspend fun response(respBody: OneBotV11ActionResp) {
        TODO("Not yet implemented")
    }
}