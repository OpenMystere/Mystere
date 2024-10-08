package io.github.mystere.core.util

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

expect fun UniHttpClient(config: HttpClientConfig<*>.() -> Unit = { }): HttpClient
expect fun _WebsocketClient(): HttpClient


fun UniWebsocketClient(config: WebSockets.Config.() -> Unit = {
    contentConverter = KotlinxWebsocketSerializationConverter(MystereJson)
}): HttpClient = _WebsocketClient().config {
    install(WebSockets, config)
}

const val MystereJsonClassDiscriminator: String = "_msty"

val MystereJson = Json {
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    classDiscriminator = MystereJsonClassDiscriminator
}

fun HttpClientConfig<*>.withJsonContent() {
    install(createClientPlugin("JsonContent") {
        onRequest { request, _ ->
            request.contentType(ContentType.Application.Json)
        }
    })
}

fun HttpClientConfig<*>.withContentNegotiation(json: Json = MystereJson) {
    install(ContentNegotiation) {
        json(json)
    }
}

fun FormBuilder.appendHttpResp(key: String, value: HttpStatement, headers: Headers = Headers.Empty) {
    append(key, ChannelProvider {
        runBlocking { value.execute().bodyAsChannel() }
    }, headers)
}

fun FormBuilder.appendFile(key: String, value: Path, headers: Headers = Headers.Empty) {
    appendInput(key, headers) {
        SystemFileSystem.source(value).buffered()
    }
}

suspend inline fun <reified T: Any> DefaultClientWebSocketSession.sendWithLog(log: KLogger, data: T) {
    val message = MystereJson.encodeToString(data)
    log.debug { "send WebSocket message: $message" }
    send(Frame.Text(message))
}

val DefaultHttpClient: HttpClient by lazy {
    UniHttpClient()
}
