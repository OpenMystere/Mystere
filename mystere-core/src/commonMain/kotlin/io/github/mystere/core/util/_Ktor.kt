package io.github.mystere.core.util

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
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
    useArrayPolymorphism = true
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