package io.github.mystere.qq.util

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun HttpClient(): HttpClient
expect fun WebsocketClient(): HttpClient

val JsonGlobal = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = true
}

val HttpClient.withJsonContent: HttpClient get() {
    return config {
        install(createClientPlugin("json-header") {
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