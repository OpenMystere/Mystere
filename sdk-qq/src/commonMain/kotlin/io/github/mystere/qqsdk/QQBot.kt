package io.github.mystere.qqsdk

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.lazyMystereScope
import io.github.mystere.qqsdk.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qqsdk.qqapi.http.QQAuthAPI
import io.github.mystere.qqsdk.qqapi.http.QQBotAPI
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketConnection
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

data class QQBot private constructor(
    private val config: IConfig,
    private val EventChannel: Channel<QQBotWebsocketPayload>,
) : IMystereBot<QQBotWebsocketPayload>, ReceiveChannel<QQBotWebsocketPayload> by EventChannel {
    internal constructor(config: IConfig): this(config, Channel())

    override val botId: String = config.appId

    override val log = KotlinLogging.logger("QQBot(id: ${config.appId})")

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1

    private val coroutineScope: CoroutineScope by lazyMystereScope()

    private var websocket: QQBotWebsocketConnection? = null

    private val AuthAPI by lazy {
        QQAuthAPI(
            logger = log,
        )
    }
    val BotAPI by lazy {
        QQBotAPI(
            logger = log,
            appId = config.appId,
            accessTokenProvider = {
                accessToken
            }
        )
    }

    override suspend fun connect() {
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                if (accessTokenExpire >= 0) {
                    delay(max(accessTokenExpire - 55, accessTokenExpire) * 1000L)
                    accessTokenExpire = -1
                    continue
                }
                try {
                    AuthAPI.getAppAccessToken(
                        AppAccessTokenReqDto(
                            config.appId, config.clientSecret
                        )
                    ).let {
                        accessToken = it.accessToken
                        accessTokenExpire = it.expiresIn
                    }
                    log.info { "Token refreshed!" }
                } catch (e: Exception) {
                    log.warn(e) { "Failed to refresh token, retry in 10s..." }
                    accessTokenExpire = 10
                    return@launch
                }
                if (websocket == null) {
                    websocket = QQBotWebsocketConnection(
                        log = log,
                        url = BotAPI.gateway().url,
                        isPrivate = config.private,
                        channel = EventChannel,
                    ) provider@{
                        return@provider accessToken
                    }
                }
            }
        }
    }

    override suspend fun disconnect() {
        coroutineScope.cancel()
    }

    sealed interface IConfig {
        val appId: String
        val clientSecret: String
        val private: Boolean
    }
    @Serializable
    data class Config(
        @SerialName("app-id")
        override val appId: String,
        @SerialName("client-secret")
        override val clientSecret: String,
        @SerialName("private")
        override val private: Boolean = false,
    ): IConfig
    @Serializable
    data class MutableConfig internal constructor(
        @SerialName("app-id")
        override var appId: String,
        @SerialName("client-secret")
        override var clientSecret: String,
        @SerialName("private")
        override var private: Boolean = false,
    ): IConfig

    companion object {
        fun create(
            config: MutableConfig.() -> Unit,
        ): QQBot {
            return QQBot(
                MutableConfig("", "").also(config).copy().also {
                    if (it.appId.isBlank()) {
                        throw IllegalArgumentException("empty appId of a QQBot")
                    }
                    if (it.clientSecret.isBlank()) {
                        throw IllegalArgumentException("empty clientSecret of a QQBot")
                    }
                }
            )
        }

        fun create(
            config: Config,
        ): QQBot {
            return QQBot(config)
        }
    }
}
