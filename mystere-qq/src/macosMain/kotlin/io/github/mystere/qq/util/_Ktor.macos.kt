package io.github.mystere.qq.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*

actual fun HttpClient(): HttpClient = HttpClient(CIO) {

}
actual fun WebsocketClient(): HttpClient = HttpClient(CIO) {
    install(WebSockets)
}