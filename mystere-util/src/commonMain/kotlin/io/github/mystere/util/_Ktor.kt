package io.github.mystere.util

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun HttpClient(): HttpClient
expect fun _WebsocketClient(): HttpClient
fun WebsocketClient(config: WebSockets.Config.() -> Unit = {
    contentConverter = KotlinxWebsocketSerializationConverter(JsonGlobal)
}): HttpClient = _WebsocketClient().config {
    install(WebSockets, config)
}

val JsonGlobal = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = true
    useArrayPolymorphism = true
}

val HttpClient.withJsonContent: HttpClient get() {
    return config {
        install(createClientPlugin("JsonContent") {
            onRequest { request, _ ->
                request.contentType(ContentType.Application.Json)
            }
        })
    }
}

val HttpClient.withContentNegotiation: HttpClient get() {
    return config {
        install(ContentNegotiation) {
            json(JsonGlobal)
        }
    }
}