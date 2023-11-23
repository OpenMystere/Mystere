package io.github.mystere.onebot.v11

import io.github.mystere.core.util.MystereJson
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class WebsocketConnectionTest {
    @Test
    fun test() {
        runBlocking {
            embeddedServer(CIO, 5710) {
                install(WebSockets) {
                    contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
                }
                routing {
                    webSocket("/") {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) {
                                continue
                            }
                            println(frame.readText())
                        }
                    }
                }
            }.start(wait = true)
        }
    }
}