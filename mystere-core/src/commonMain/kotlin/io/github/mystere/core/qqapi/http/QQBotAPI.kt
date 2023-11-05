package io.github.mystere.core.qqapi.http

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.core.qqapi.dto.AppAccessTokenReqDto
import io.github.mystere.core.qqapi.dto.AppAccessTokenRespDto
import io.github.mystere.core.util.HttpClient
import io.github.mystere.core.util.withContentNegotiation
import io.github.mystere.core.util.withJsonContent

interface IQQBotAPI {
    @POST("/app/getAppAccessToken")
    suspend fun getAppAccessToken(
        @Body dto: AppAccessTokenReqDto
    ): AppAccessTokenRespDto
}

val QQBotAPI: IQQBotAPI by lazy {
    Ktorfit.Builder()
        .baseUrl("https://bots.qq.com")
        .httpClient(
            HttpClient()
                .withJsonContent
                .withContentNegotiation
        )
        .build()
        .create()
}
