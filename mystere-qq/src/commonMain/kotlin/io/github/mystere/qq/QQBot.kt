package io.github.mystere.qq

import io.github.mystere.core.IMystereBot
import io.github.mystere.core.MystereCore
import io.github.mystere.core.MystereScope
import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.v11.HeartbeatStatus
import io.github.mystere.onebot.v11.IOneBotV11Connection
import io.github.mystere.onebot.v11.MetaHeartbeat
import io.github.mystere.onebot.v11.connection.applySelfIdHeader
import io.github.mystere.onebot.v12.IOneBotV12Connection
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.http.QQAuthAPI
import io.github.mystere.qq.qqapi.http.QQBotAPI
import io.github.mystere.qq.qqapi.websocket.QQBotWebsocketConnection
import io.github.mystere.util.withLogging
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

data class QQBot internal constructor(
    private val config: Config,
    override val connection: IOneBotConnection,
): IMystereBot {
    override val botId: String = config.appId

    private val log = KotlinLogging.logger("QQBot(id: ${config.appId})")

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1

    private val coroutineScope: CoroutineScope by lazy { MystereScope() }

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

    override fun connect() {
        coroutineScope.launch(Dispatchers.IO) {
            connection.connect {
                applySelfIdHeader(botId)
                withLogging(log, MystereCore.Debug)
            }
            // 心跳
            delay(5_000)
            while (true) {
                when (connection) {
                    is IOneBotV11Connection -> {
                        connection.sendEvent(MetaHeartbeat(
                            botId.toLong(), HeartbeatStatus(
                                online = true, good = true
                            )
                        ))
                    }
                    is IOneBotV12Connection -> {
                        // TODO
                    }
                }
                delay(30_000)
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
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
        coroutineScope.cancel()
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
            connection: IOneBotConnection,
            config: Config.() -> Unit,
        ): QQBot {
            return QQBot(
                Config("", "").also(config).copy().also {
                    if (it.appId.isBlank()) {
                        throw IllegalArgumentException("empty appId of a QQBot")
                    }
                    if (it.clientSecret.isBlank()) {
                        throw IllegalArgumentException("empty clientSecret of a QQBot")
                    }
                },
                connection,
            )
        }
        fun create(
            config: Config,
            connection: IOneBotConnection,
        ): QQBot {
            return QQBot(config, connection)
        }
    }
}
