package io.github.mystere.qq.qqapi.http

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.qq.qqapi.dto.*
import io.github.mystere.qq.qqapi.websocket.message.QQMessageContent
import io.github.mystere.serialization.cqcode.CQCodeMessage
import io.github.mystere.serialization.cqcode.CQCodeMessageItem
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
) {
    _channelsMessage(channelId, MultiPartFormDataContent(formData {
        content?.let { append("content", JsonGlobal.encodeToString(QQMessageContent(content))) }
        embed?.let { append("embed", JsonGlobal.encodeToString(embed)) }
        ark?.let { append("ark", JsonGlobal.encodeToString(ark)) }
        messageReference?.let { append("messageReference", JsonGlobal.encodeToString(messageReference)) }
        eventId?.let { append("eventId", eventId) }
        markdown?.let { append("markdown", JsonGlobal.encodeToString(markdown)) }
        for (item in content ?: return@formData) {
            when (item) {
                is CQCodeMessageItem.Image -> when {
                    item.file.startsWith("file:///") -> {
                        val path = with(item.file.substring(8)) {
                            if (contains(":")) {
                                this
                            } else {
                                "/$this"
                            }
                        }
                        append("file_image", SystemFileSystem.source(Path(path)).buffered().readByteArray())
                    }
                    item.file.startsWith("http://") -> {
                        append("image", item.file)
                    }
                    item.file.startsWith("base64://") -> {
                        val base64Content = item.file.substring(9)
                        append("file_image", Base64.decode(base64Content))
                    }
                }
            }
        }
    }))
}
