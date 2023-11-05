package io.github.mystere.core

import io.github.mystere.core.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.core.qqapi.http.QQBotAPI
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlin.math.max

@OptIn(ExperimentalStdlibApi::class)
sealed interface IQQBot: AutoCloseable {
    suspend fun connect()

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    companion object {
        fun create(appId: String, clientSecret: String): IQQBot {
            return QQBot(appId, clientSecret)
        }
    }
}

data class QQBot internal constructor(
    private val appId: String,
    private val clientSecret: String,
): IQQBot {
    private val log = KotlinLogging.logger("QQBot(id: $appId)")

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
                    QQBotAPI.getAppAccessToken(
                        AppAccessTokenReqDto(
                        appId, clientSecret
                    )
                    ).let {
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
