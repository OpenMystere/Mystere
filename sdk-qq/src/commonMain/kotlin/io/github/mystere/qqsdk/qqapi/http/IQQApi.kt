package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.qqsdk.qqapi.dto.*
import io.github.mystere.core.util.MystereJson
import io.ktor.client.request.forms.*
import io.ktor.http.*
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
    msgId: String? = null,
    markdown: MessageMarkdown? = null,
    images: List<String> = emptyList(),
) {
    _channelsMessage(channelId, MultiPartFormDataContent(formData {
        content?.let { append("content", it) }
        embed?.let { append("embed", MystereJson.encodeToString(it)) }
        ark?.let { append("ark", MystereJson.encodeToString(it)) }
        messageReference?.let { append("message_reference", MystereJson.encodeToString(it)) }
        markdown?.let { append("markdown", MystereJson.encodeToString(it)) }
        msgId?.let { append("msg_id", it) }
        var size = 0
        for (item in images) {
            when  {
                item.startsWith("file:///") -> {
                    val path = with(item.substring(8)) {
                        if (contains(":")) this else "/$this"
                    }
                    append(
                        "file_image", SystemFileSystem.source(Path(path)).buffered().readByteArray(),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                }
                item.startsWith("https://") || item.startsWith("http://") -> {
                    append("image", item)
                }
                item.startsWith("base64://") -> {
                    append(
                        "file_image", Base64.decode(item.substring(9)),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                }
            }
        }
    }))
}
