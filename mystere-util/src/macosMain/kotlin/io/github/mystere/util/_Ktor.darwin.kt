package io.github.mystere.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*

actual fun HttpClient(): HttpClient = HttpClient(CIO) {

}
actual fun _WebsocketClient(): HttpClient = HttpClient(CIO) {

}