package io.github.mystere.qq.qqapi.websocket

import io.github.mystere.qq.qqapi.websocket.message.Intent
import io.github.mystere.qq.qqapi.websocket.message.OpCode10
import io.github.mystere.qq.qqapi.websocket.message.OpCode2
import io.github.mystere.qq.util.JsonGlobal
import io.github.mystere.qq.util.WebsocketClient
import io.github.mystere.qq.util.withLogging
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@OptIn(ExperimentalStdlibApi::class)
class QQBotWebsocketConnection internal constructor(
    private val log: KLogger,
    private val url: String,
    private val accessTokenProvider: () -> String,
): AutoCloseable {
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    private val WebsocketClient: HttpClient by lazy {
        WebsocketClient()
            .config {
                install(WebSockets) {
                    contentConverter = KotlinxWebsocketSerializationConverter(JsonGlobal)
                }
            }
            .withLogging(log)
    }

    private var s: Long? = null
    init {
        scope.launch(Dispatchers.Default) {
            var WebsocketConnectJob: Job? = null
            var Connection: DefaultClientWebSocketSession? = null
            while (true) {
                Connection?.close()
                Connection = WebsocketClient.webSocketSession(url)
                Connection.sendWithLog(QQBotWebsocketPayload(
                    opCode = QQBotWebsocketPayload.OpCode.Identify,
                    data = OpCode2.IdentifyPayload(
                        token = "QQBot ${accessTokenProvider()}",
                        intents = Intent.DEFAULT,
                        properties = buildJsonObject {
                            // TODO: 搭配 buildkonfing 动态版本名
                            put("\$client", JsonPrimitive("Mystere v${"1.0.0-alpha01"}"))
                            put("\$platform", JsonPrimitive(0))
                        },
                    ),
                    serializer = OpCode2.IdentifyPayload.serializer(),
                ))
                WebsocketConnectJob?.cancelAndJoin()
                var exception: Throwable? = null
                WebsocketConnectJob = scope.launch(Dispatchers.Default) {
                    try {
                        while (true) {
                            log.debug { "waiting for new WebSocket message..." }
                            val rawMessage = Connection.incoming.receive() as Frame.Text
                            log.debug { "new WebSocket message!" }
                            val payload = try {
                                JsonGlobal.decodeFromString(
                                    QQBotWebsocketPayload.serializer(),
                                    rawMessage.readText().also {
                                        log.debug { "new WebSocket message content: $it" }
                                    },
                                )
                            } catch (e: Exception) {
                                log.warn { "failed during parsing WebSocket message!" }
                                continue
                            }
                            s = payload.s
                            try {
                                processMessage(Connection, payload)
                            } catch (e: Exception) {
                                log.warn { "failed during process WebSocket message! (type: ${payload.type})" }
                                continue
                            }
                        }
                    } catch (e: Throwable) {
                        exception = e
                    }
                }
                WebsocketConnectJob.join()
                log.warn(exception) { "WebSocket disconnected, reconnect in 10s..." }
                delay(10_000)
            }
        }
    }

    private var heartbeatJob: Job? = null
    private suspend fun processMessage(
        connection: DefaultClientWebSocketSession,
        message: QQBotWebsocketPayload,
    ) {
        when (message.opCode) {
            QQBotWebsocketPayload.OpCode.Hello -> {
                heartbeatJob?.cancelAndJoin()
                heartbeatJob = scope.launch {
                    while (true) {
                        connection.sendWithLog(QQBotWebsocketPayload(
                            opCode = QQBotWebsocketPayload.OpCode.Heartbeat,
                            data = JsonPrimitive(s)
                        ))
                        delay(JsonGlobal.decodeFromJsonElement(
                            OpCode10.QQHelloMessage.serializer(), message.data
                        ).heartbeatInterval)
                    }
                }
            }
            else -> { }
        }
    }


    override fun close() {
        scope.cancel()
        WebsocketClient.close()
    }

    private suspend inline fun <reified T: Any> DefaultClientWebSocketSession.sendWithLog(data: T) {
        val message = JsonGlobal.encodeToString(data)
        log.debug { "send WebSocket message: $message" }
        send(message)
    }
}

@Serializable
data class QQBotWebsocketPayload(
    @SerialName("op")
    val opCode: OpCode,
    @SerialName("s")
    val s: Long? = null,
    @SerialName("t")
    val type: String? = "",
    @SerialName("d")
    val data: JsonElement = JsonNull,
) {
    @Serializable(with = OpCode.OpCodeSerializer::class)
    enum class OpCode(
        val rawCode: Int,
    ) {
        Dispatch(0),
        Heartbeat(1),
        Identify(2),
        Resume(6),
        Reconnect(7),
        InvalidSession(9),
        Hello(10),
        HeartbeatACK(11),
        HTTPCallbackACK(12),
        Unknown(-1);

        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        object OpCodeSerializer: KSerializer<OpCode> {
            override val descriptor: SerialDescriptor = buildSerialDescriptor("opcode", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder): OpCode {
                val rawCode = decoder.decodeInt()
                return OpCode.entries.find {
                    it.rawCode == rawCode
                } ?: QQBotWebsocketPayload.OpCode.Unknown
            }

            override fun serialize(encoder: Encoder, value: OpCode) {
                encoder.encodeInt(value.rawCode)
            }
        }
    }
}

fun <T: Any> QQBotWebsocketPayload(
    opCode: QQBotWebsocketPayload.OpCode,
    s: Long? = null,
    type: String? = null,
    data: T,
    serializer: KSerializer<T>,
) = QQBotWebsocketPayload(
    opCode, s, type,
    JsonGlobal.encodeToJsonElement(
        serializer, data
    )
)
