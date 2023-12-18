package io.github.mystere.core.util

import io.ktor.client.*
import io.ktor.utils.io.streams.*
import java.io.File

actual fun UniHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(io.ktor.client.engine.cio.CIO, config)
actual fun _WebsocketClient(): HttpClient = HttpClient(io.ktor.client.engine.cio.CIO) {

}

fun main() {
    File("test").inputStream().asInput()
}