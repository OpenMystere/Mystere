package io.github.mystere.qq.util

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.plugins.websocket.*

actual fun HttpClient(): HttpClient = HttpClient(WinHttp) {

}
actual fun WebsocketClient(): HttpClient = HttpClient(WinHttp) {
    install(WebSockets)
}