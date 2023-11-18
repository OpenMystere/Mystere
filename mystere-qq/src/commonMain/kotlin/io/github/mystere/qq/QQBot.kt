package io.github.mystere.qq

import io.github.mystere.core.IMystereBot
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.http.QQAuthAPI
import io.github.mystere.qq.qqapi.http.QQBotAPI
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

data class QQBot internal constructor(
    private val config: Config,
): IMystereBot {
    override val botId: String = config.appId

    private val log = KotlinLogging.logger("QQBot(id: ${config.appId})")

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }
    private var websocket: QQBotWebsocketConnection? = null

    private val QQAuthAPI by lazy {
        QQAuthAPI(
            logger = log,
        )
    }
    private val QQBotAPI by lazy {
        QQBotAPI(
            logger = log,
            appId = config.appId,
            accessTokenProvider = {
                accessToken
            }
        )
    }

    private var _OneBotConnection: IOneBotConnection? = null
    private val OneBotConnection: IOneBotConnection get() = _OneBotConnection!!

    override fun connect(connection: IOneBotConnection) {
        scope.launch(Dispatchers.IO) {
            _OneBotConnection = connection
            while (true) {
                if (accessTokenExpire >= 0) {
                    delay(max(accessTokenExpire - 55, accessTokenExpire) * 1000L)
                    accessTokenExpire = -1
                    continue
                }
                try {
                    QQAuthAPI.getAppAccessToken(AppAccessTokenReqDto(
                        config.appId, config.clientSecret
                    )).let {
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
                        url = QQBotAPI.gateway().url,
                    ) provider@{
                        return@provider accessToken
                    }
                }
            }
        }
    }

    override fun disconnect() {
        scope.cancel()
    }

    @Serializable
    data class Config internal constructor(
        @SerialName("app-id")
        val appId: String,
        @SerialName("client-secret")
        val clientSecret: String,
    )

    companion object {
        fun create(
            config: Config.() -> Unit
        ): QQBot {
            return QQBot(Config("", "").also(config).copy().also {
                if (it.appId.isBlank()) {
                    throw IllegalArgumentException("empty appId of a QQBot")
                }
                if (it.clientSecret.isBlank()) {
                    throw IllegalArgumentException("empty clientSecret of a QQBot")
                }
            })
        }
        fun create(
            config: Config
        ): QQBot {
            return QQBot(config)
        }
    }
}
