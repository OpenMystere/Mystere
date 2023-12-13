package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.github.mystere.core.MystereCore
import io.github.mystere.core.util.*
import io.github.mystere.qqsdk.qqapi.dto.QQCodeMessageDataDto
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer


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
        .converterFactories(QQOpenApiConverterFactory)
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
        .converterFactories(QQOpenApiConverterFactory)
        .baseUrl("https://api.sgroup.qq.com/")
        .httpClient(
            UniHttpClient(config)
        )
        .build()
        .create()
}

object QQOpenApiConverterFactory: Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *> {
        return QQOpenApiConverter(typeData, ktorfit)
    }

    class QQOpenApiConverter(
        private val typeData: TypeData,
        private val ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, Any> {
        @Suppress("OVERRIDE_DEPRECATION")
        override suspend fun convert(response: HttpResponse): Any {
            val body = MystereJson.decodeFromString<JsonElement>(response.bodyAsText())
            if (body is JsonObject && body.containsKey("code") && body.containsKey("message")) {
                throw QQCodeMessageDataDto(
                    code = body["code"]!!.jsonPrimitive.int,
                    message = body["message"]!!.jsonPrimitive.content,
                    data = body["data"] ?: JsonNull,
                )
            }
            return MystereJson.decodeFromJsonElement(
                MystereJson.serializersModule.serializer(typeData.typeInfo.kotlinType!!), body
            ) as Any
        }
    }
}
