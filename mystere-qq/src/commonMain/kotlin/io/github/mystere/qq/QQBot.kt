package io.github.mystere.qq

import io.github.mystere.core.MystereBot
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.http.QQBotAPI
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

sealed interface IQQBot: MystereBot {
    companion object {
        fun create(
            config: Config.() -> Unit
        ): QQBot {
            return QQBot(Config().also(config).copy())
        }
        fun create(
            config: Config
        ): QQBot {
            return QQBot(config)
        }
    }

    @Serializable
    data class Config internal constructor(
        @SerialName("app-id")
        val botId: String = "",
        @SerialName("client-secret")
        val clientSecret: String = "",
    )
}

data class QQBot internal constructor(
    private val config: IQQBot.Config,
): IQQBot {
    private val log = KotlinLogging.logger("QQBot(id: ${config.botId})")

    private var accessToken: String = ""
    private var accessTokenExpire: Int = -1

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override suspend fun connect() {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (accessTokenExpire >= 0) {
                    delay(max(accessTokenExpire - 55, accessTokenExpire) * 1000L)
                    accessTokenExpire = -1
                    continue
                }
                try {
                    QQBotAPI.getAppAccessToken(AppAccessTokenReqDto(
                        config.botId, config.clientSecret
                    )).let {
                        accessToken = it.accessToken
                        accessTokenExpire = it.expiresIn
                    }
                    log.info { "Token refreshed!" }
                } catch (e: Exception) {
                    log.warn(e) { "Failed to refresh token, retry in 10s..." }
                    accessTokenExpire = 10
                }
            }
        }
    }

    override fun close() {
        scope.cancel()
    }
}
