package io.github.mystere.qq.qqapi.http

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.qq.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.qq.qqapi.dto.AppAccessTokenRespDto
import io.github.mystere.qq.util.HttpClient
import io.github.mystere.qq.util.withContentNegotiation
import io.github.mystere.qq.util.withJsonContent
import io.github.mystere.qq.util.withLogging

interface IQQBotAPI {
    @POST("/app/getAppAccessToken")
    suspend fun getAppAccessToken(
        @Body dto: AppAccessTokenReqDto
    ): AppAccessTokenRespDto
}

val QQBotAPI: IQQBotAPI
    get() {
    return Ktorfit.Builder()
        .baseUrl("https://bots.qq.com")
        .httpClient(
            HttpClient()
                .withJsonContent
                .withContentNegotiation
                .withLogging("QQBotAPI")
        )
        .build()
        .create()
}
