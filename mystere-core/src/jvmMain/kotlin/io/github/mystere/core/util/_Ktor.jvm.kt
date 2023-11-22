package io.github.mystere.core.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun UniHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(CIO, config)
actual fun _WebsocketClient(): HttpClient = HttpClient(CIO) {

}