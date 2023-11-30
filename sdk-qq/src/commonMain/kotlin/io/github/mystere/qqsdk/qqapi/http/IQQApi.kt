package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import io.github.mystere.core.util.DefaultHttpClient
import io.github.mystere.qqsdk.qqapi.dto.*
import io.github.mystere.core.util.MystereJson
import io.github.mystere.core.util.logger
import io.github.mystere.qqsdk.qqapi.data.MessageArk
import io.github.mystere.qqsdk.qqapi.data.MessageEmbed
import io.github.mystere.qqsdk.qqapi.data.MessageMarkdown
import io.github.mystere.qqsdk.qqapi.data.MessageReference
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.encodeToString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


val IQQAuthAPI.log: KLogger by lazy {
    KotlinLogging.logger(IQQAuthAPI::class.qualifiedName!!)
}

interface IQQAuthAPI {
    @POST("app/getAppAccessToken")
    suspend fun getAppAccessToken(
        @Body dto: AppAccessTokenReqDto
    ): AppAccessTokenRespDto
}

val IQQBotAPI.log: KLogger by lazy {
    KotlinLogging.logger(IQQBotAPI::class.qualifiedName!!)
}

interface IQQBotAPI {
    @GET("gateway")
    suspend fun gateway(): GatewayRespDto

    @POST("channels/{channel_id}/messages")
    suspend fun _messageIO_channel(
        @de.jensklingenberg.ktorfit.http.Path("channel_id")
        channelId: String,
        @Body
        body: MultiPartFormDataContent,
    ): OpCode0.Message
    @POST("channels/{channel_id}/messages")
    suspend fun _messageIO_channel(
        @de.jensklingenberg.ktorfit.http.Path("channel_id")
        channelId: String,
        @Body message: MessageRequestDto,
    ): OpCode0.Message

    @POST("v2/groups/{group_openid}/messages")
    suspend fun _messageIO_groups(
        @de.jensklingenberg.ktorfit.http.Path("group_openid")
        groupOpenId: String,
        @Body
        body: MultiPartFormDataContent,
    ): OpCode0.Message
}

@OptIn(ExperimentalEncodingApi::class)
suspend fun IQQBotAPI.messageIO_channel(
    channelId: String,
//    msgType: Int,
    content: String? = null,
    embed: MessageEmbed? = null,
    ark: MessageArk? = null,
    messageReference: MessageReference? = null,
    msgId: String? = null,
    eventId: String? = null,
    markdown: MessageMarkdown? = null,
    images: List<String> = emptyList(),
): OpCode0.Message {
    return _messageIO_channel(channelId, MultiPartFormDataContent(formData {
//            append("msg_type", msgType)
        content?.takeIf { it.isNotBlank() }?.let { append("content", it) }
        embed?.let { append("embed", MystereJson.encodeToString(it)) }
        ark?.let { append("ark", MystereJson.encodeToString(it)) }
        messageReference?.let { append("message_reference", MystereJson.encodeToString(it)) }
        markdown?.let { append("markdown", MystereJson.encodeToString(it)) }
        msgId?.let { append("msg_id", it) }
        eventId?.let { append("event_id", it) }
        var size = 0
        for (item in images) {
            when  {
                item.startsWith("file:///") -> {
                    val path = with(item.substring(8)) {
                        if (contains(":")) this else "/$this"
                    }
                    append(
                        "file_image",
                        SystemFileSystem.source(Path(path)).buffered().readByteArray(),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                }
                item.startsWith("https://") || item.startsWith("http://") -> {
                    append(
                        "file_image",
                        runBlocking {
                            DefaultHttpClient.get(item).body<ByteArray>()
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                }
                item.startsWith("base64://") -> {
                    append(
                        "file_image",
                        Base64.decode(item.substring(9)),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                }
                else -> {
                    log.warn { "Unknown image type: ${item.substring(0, 20)}..." }
                }
            }
        }
    }))
}
