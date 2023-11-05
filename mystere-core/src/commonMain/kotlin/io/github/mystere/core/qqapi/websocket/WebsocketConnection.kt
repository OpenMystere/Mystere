package io.github.mystere.core.qqapi.websocket

import io.github.mystere.core.util.WebsocketClient
import io.ktor.client.*
import io.ktor.client.plugins.logging.*

@ExperimentalStdlibApi
class WebsocketConnection internal constructor(
    private val url: String,
): AutoCloseable {
    private val WebsocketClient: HttpClient = WebsocketClient().config {
        install(Logging) {

        }
    }

    override fun close() {
        WebsocketClient.close()
    }
}