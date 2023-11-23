package io.github.mystere.core.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

expect fun UniHttpClient(config: HttpClientConfig<*>.() -> Unit = { }): HttpClient
expect fun _WebsocketClient(): HttpClient
fun UniWebsocketClient(config: WebSockets.Config.() -> Unit = {
    contentConverter = KotlinxWebsocketSerializationConverter(JsonGlobal)
}): HttpClient = _WebsocketClient().config {
    install(WebSockets, config)
}

val JsonGlobal = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    classDiscriminator = "_ktx"
}

fun HttpClientConfig<*>.withJsonContent() {
    install(createClientPlugin("JsonContent") {
        onRequest { request, _ ->
            request.contentType(ContentType.Application.Json)
        }
    })
}

fun HttpClientConfig<*>.withContentNegotiation(json: Json = JsonGlobal) {
    install(ContentNegotiation) {
        json(json)
    }
}

suspend inline fun <reified T: Any> DefaultClientWebSocketSession.sendWithLog(log: KLogger, data: T) {
    val message = JsonGlobal.encodeToString(data)
    log.debug { "send WebSocket message: $message" }
    send(Frame.Text(message))
}
