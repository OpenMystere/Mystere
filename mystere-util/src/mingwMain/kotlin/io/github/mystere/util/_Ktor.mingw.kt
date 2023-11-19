package io.github.mystere.util

import io.ktor.client.*
import io.ktor.client.engine.winhttp.*

actual fun UniHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(WinHttp, config)
actual fun _WebsocketClient(): HttpClient = HttpClient(WinHttp) {

}