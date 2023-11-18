package io.github.mystere.util

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*

actual fun HttpClient(): HttpClient = HttpClient(WinHttp) {

}
actual fun _WebsocketClient(): HttpClient = HttpClient(WinHttp) {

}