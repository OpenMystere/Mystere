package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.qqsdk.qqapi.dto.*
import io.github.mystere.core.util.JsonGlobal
import io.ktor.client.request.forms.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.encodeToString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface IQQAuthAPI {
    @POST("app/getAppAccessToken")
    suspend fun getAppAccessToken(
        @Body dto: AppAccessTokenReqDto
    ): AppAccessTokenRespDto
}

interface IQQBotAPI {
    @GET("gateway")
    suspend fun gateway(): GatewayRespDto

    @POST("channels/{channel_id}/messages")
    suspend fun _channelsMessage(
        @de.jensklingenberg.ktorfit.http.Path("channel_id")
        channelId: String,
        @Body
        body: MultiPartFormDataContent,
    )
}
@OptIn(ExperimentalEncodingApi::class)
suspend fun IQQBotAPI.channelsMessage(
    channelId: String,
    content: String? = null,
    embed: MessageEmbed? = null,
    ark: MessageArk? = null,
    messageReference: MessageReference? = null,
    eventId: String? = null,
    markdown: MessageMarkdown? = null,
    images: List<String> = emptyList(),
) {
    _channelsMessage(channelId, MultiPartFormDataContent(formData {
        content?.let { append("content", content) }
        embed?.let { append("embed", JsonGlobal.encodeToString(embed)) }
        ark?.let { append("ark", JsonGlobal.encodeToString(ark)) }
        messageReference?.let { append("messageReference", JsonGlobal.encodeToString(messageReference)) }
        eventId?.let { append("eventId", eventId) }
        markdown?.let { append("markdown", JsonGlobal.encodeToString(markdown)) }
        for (item in images) {
            when  {
                item.startsWith("file:///") -> {
                    val path = with(item.substring(8)) {
                        if (contains(":")) this else "/$this"
                    }
                    append("file_image", SystemFileSystem.source(Path(path)).buffered().readByteArray())
                }
                item.startsWith("https://") || item.startsWith("http://") -> {
                    append("image", item)
                }
                item.startsWith("base64://") -> {
                    val base64Content = item.substring(9)
                    append("file_image", Base64.decode(base64Content))
                }
            }
        }
    }))
}
