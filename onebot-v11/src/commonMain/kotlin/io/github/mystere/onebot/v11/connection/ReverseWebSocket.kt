package io.github.mystere.onebot.v11.connection

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotEvent
import io.github.mystere.onebot.OneBotConnectionException
import io.github.mystere.onebot.v11.*
import io.github.mystere.serialization.cqcode.CQCodeMessageItem
import io.github.mystere.serialization.cqcode.asMessage
import io.github.mystere.util.JsonGlobal
import io.github.mystere.util.UniWebsocketClient
import io.ktor.client.*
import io.ktor.client.plugins.api.*

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.KSerializer


fun HttpClientConfig<*>.applySelfIdHeader(selfId: String) {
    install(createClientPlugin("OneBotV11-SelfIdHeader") {
        onRequest { request, _ ->
            request.header("X-Self-ID", selfId)
        }
    })
}

internal class ReverseWebSocketConnection(
    override val originConfig: OneBotV11Connection.ReverseWebSocket,
    actionChannel: Channel<IOneBotAction>,
): IOneBotV11Connection(originConfig, actionChannel) {
    private var _WebsocketClient: HttpClient? = null
    private val WebsocketClient: HttpClient get() = _WebsocketClient!!

    private var _UniWebsocket: DefaultClientWebSocketSession? = null
    private var _ApiWebsocket: DefaultClientWebSocketSession? = null
    private val ApiWebsocket: DefaultClientWebSocketSession get() = (_ApiWebsocket ?: _UniWebsocket)!!
    private var _EventWebsocket: DefaultClientWebSocketSession? = null
    private val EventWebsocket: DefaultClientWebSocketSession get() = (_EventWebsocket ?: _UniWebsocket)!!

    override suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit) {
        _WebsocketClient = UniWebsocketClient().config(httpClient)
        if (originConfig.url != null) {
            _UniWebsocket?.cancel()
            _UniWebsocket = WebsocketClient.webSocketSession {
                this.url.takeFrom(Url(originConfig.url))
            }
        } else if (originConfig.apiUrl != null && originConfig.eventUrl != null) {
            _ApiWebsocket?.cancel()
            _ApiWebsocket = WebsocketClient.webSocketSession {
                this.url.takeFrom(Url(originConfig.apiUrl))
            }
            _EventWebsocket?.cancel()
            _EventWebsocket = WebsocketClient.webSocketSession {
                this.url.takeFrom(Url(originConfig.eventUrl))
            }
        } else {
            throw OneBotConnectionException("url not set or apiUrl and eventUrl both not set!")
        }
    }

    override suspend fun <T: IOneBotEvent> onReceiveEvent(event: T, serializer: KSerializer<T>) {
        EventWebsocket.send(Frame.Text(
            JsonGlobal.encodeToString(serializer, event)
        ))
        when (event) {
            is Message -> {
                if (event.messageType == MessageType.guild) {
                    actionChannel.send(IOneBotV11Action(
                        params = SendGuildChannelMsg(
                            guildId = event.guildId!!,
                            channelId = event.channelId!!,
                            message = CQCodeMessageItem.Text("阿巴阿巴").asMessage(),
                        )
                    ))
                }
            }
        }
    }
}