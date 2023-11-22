package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.Ktorfit
import io.github.mystere.core.MystereCore
import io.github.mystere.core.util.UniHttpClient
import io.github.mystere.core.util.withContentNegotiation
import io.github.mystere.core.util.withJsonContent
import io.github.mystere.core.util.withLogging
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*


fun QQAuthAPI(
    logger: KLogger,
): IQQAuthAPI {
    /*
     * TODO: 这似乎是 kotlin 编译器的 bug，不能直接在 UniHttpClient 函数处使用 lambda，否则报错：
     * Inherited platform declarations clash: The following declarations have the same JVM signature
     */
    val config: HttpClientConfig<*>.() -> Unit = {
        withContentNegotiation()
        withJsonContent()
        withLogging(logger, MystereCore.Debug)
    }
    return Ktorfit.Builder()
        .baseUrl("https://bots.qq.com/")
        .httpClient(
            UniHttpClient(config)
        )
        .build()
        .create()
}

fun QQBotAPI(
    logger: KLogger,
    appId: String,
    accessTokenProvider: () -> String,
): IQQBotAPI {
    val config: HttpClientConfig<*>.() -> Unit = {
        withJsonContent()
        withContentNegotiation()
        withLogging(logger, MystereCore.Debug)
        install(createClientPlugin("qq-auth-header") {
            onRequest { request, _ ->
                request.header("X-Union-Appid", appId)
                request.header("Authorization", "QQBot ${accessTokenProvider.invoke()}")
            }
        })
    }
    return Ktorfit.Builder()
        .baseUrl("https://api.sgroup.qq.com/")
        .httpClient(
            UniHttpClient(config)
        )
        .build()
        .create()
}
