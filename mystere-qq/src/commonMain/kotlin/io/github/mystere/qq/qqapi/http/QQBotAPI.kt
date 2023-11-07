package io.github.mystere.qq.qqapi.http

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.dto.AppAccessTokenRespDto
import io.github.mystere.qq.qqapi.dto.GatewayRespDto
import io.github.mystere.qq.util.HttpClient
import io.github.mystere.qq.util.withContentNegotiation
import io.github.mystere.qq.util.withJsonContent
import io.github.mystere.qq.util.withLogging
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*

interface IQQAuthAPI {
    @POST("app/getAppAccessToken")
    suspend fun getAppAccessToken(
        @Body dto: AppAccessTokenReqDto
    ): AppAccessTokenRespDto
}

fun QQAuthAPI(
    logger: KLogger,
): IQQAuthAPI {
    return Ktorfit.Builder()
        .baseUrl("https://bots.qq.com/")
        .httpClient(
            HttpClient()
                .withJsonContent
                .withContentNegotiation
                .withLogging(logger)
        )
        .build()
        .create()
}

interface IQQBotAPI {
    @GET("gateway")
    suspend fun gateway(): GatewayRespDto
}

fun QQBotAPI(
    logger: KLogger,
    appId: String,
    accessTokenProvider: () -> String,
): IQQBotAPI {
    return Ktorfit.Builder()
        .baseUrl("https://api.sgroup.qq.com/")
        .httpClient(
            HttpClient()
                .withJsonContent
                .withContentNegotiation
                .withLogging(logger)
                .config {
                    install(createClientPlugin("qq-auth-header") {
                        onRequest { request, _ ->
                            request.header("X-Union-Appid", appId)
                            request.header("Authorization", "QQBot ${accessTokenProvider()}")
                        }
                    })
                }
        )
        .build()
        .create()
}
