package io.github.mystere.qqsdk.qqapi.http

import de.jensklingenberg.ktorfit.http.*
import io.github.mystere.core.util.*
import io.github.mystere.qqsdk.qqapi.dto.*
import io.github.mystere.qqsdk.qqapi.data.*
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.Headers
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path as FilePath
import kotlinx.io.files.SystemFileSystem
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

    @Multipart
    @POST("channels/{channel_id}/messages")
    suspend fun _messageIO_channel(
        @Path("channel_id") channelId: String,
        @Body body: MultiPartFormDataContent,
    ): OpCode0.GuildMessage
    @POST("dms/{guild_id}/messages")
    suspend fun _messageIO_guild(
        @Path("guild_id") guildId: String,
        @Body body: MultiPartFormDataContent,
    ): OpCode0.GuildMessage
    @POST("v2/groups/{group_openid}/messages")
    suspend fun messageIO_groups(
        @Path("group_openid") groupOpenId: String,
        @Body message: GroupMessageRequestDto,
    ): OpCode0.GuildMessage

    /**
     * @see <a href="https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html#用于群聊">富文本消息 - 用于群聊</a>
     */
    @POST("v2/groups/{group_openid}/files")
    suspend fun _richMedia_groups(
        @Path("group_openid") groupOpenId: String,
        @Body body: MultiPartFormDataContent,
    ): RichMediaRespDto
    /**
     * @see <a href="https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html#用于群聊">富文本消息 - 用于群聊</a>
     */
    @POST("v2/users/{group_openid}/files")
    suspend fun _richMedia_c2c(
        @Path("group_openid") openid: String,
        @Body body: MultiPartFormDataContent,
    ): RichMediaRespDto

    /**
     * @see <a href="https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/reset.html#文字子频道">撤回消息 - 文字子频道</a>
     */
    @DELETE("channels/{channel_id}/messages/{message_id}")
    suspend fun messageDelete_channel(
        @Path("channel_id") channelId: String,
        @Path("message_id") messageId: String,
        @Query("hidetip") hidetip: Boolean = false,
    )
    @DELETE("dms/{guild_id}/messages/{message_id}")
    suspend fun messageDelete_guild(
        @Path("guild_id") guildId: String,
        @Path("message_id") messageId: String,
        @Query("hidetip") hidetip: Boolean = false,
    )

    @PUT("channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    suspend fun messageReaction_put(
        @Path("channel_id") channelId: String,
        @Path("message_id") messageId: String,
        @Path("type") type: EmojiType,
        @Path("id") id: String,
    )
    @DELETE("channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    suspend fun messageReaction_delete(
        @Path("channel_id") channelId: String,
        @Path("message_id") messageId: String,
        @Path("type") type: EmojiType,
        @Path("id") id: String,
    )
    @GET("channels/{channel_id}/messages/{message_id}/reactions/{type}/{id}")
    suspend fun messageReaction_delete(
        @Path("channel_id") channelId: String,
        @Path("message_id") messageId: String,
        @Path("type") type: EmojiType,
        @Path("id") id: String,
        @Query("cookie") cookie: String? = null,
        @Query("limit") limit: Int = 20,
    )
}

suspend fun IQQBotAPI.richMedia_groups(
    file: String,
) {

}
suspend fun IQQBotAPI.messageIO_guild(
    channelId: String,
    content: String? = null,
    embed: MessageEmbed? = null,
    ark: MessageArk? = null,
    messageReference: MessageReference? = null,
    msgId: String? = null,
    eventId: String? = null,
    markdown: MessageMarkdown? = null,
    images: List<String> = emptyList(),
): OpCode0.GuildMessage {
    return _messageIO_guild(channelId, createGuildMessageBody(
        content, embed, ark, messageReference, msgId, eventId, markdown, images
    ))
}

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
): OpCode0.GuildMessage {
    return _messageIO_channel(channelId, createGuildMessageBody(
        content, embed, ark, messageReference, msgId, eventId, markdown, images
    ))
}

@OptIn(ExperimentalEncodingApi::class)
private suspend fun IQQBotAPI.createGuildMessageBody(
    content: String? = null,
    embed: MessageEmbed? = null,
    ark: MessageArk? = null,
    messageReference: MessageReference? = null,
    msgId: String? = null,
    eventId: String? = null,
    markdown: MessageMarkdown? = null,
    images: List<String> = emptyList(),
): MultiPartFormDataContent {
    return MultiPartFormDataContent(formData {
        content?.takeIf { it.isNotBlank() }?.let { append("content", it) }
        embed?.let { append("embed", MystereJson.encodeToString(it)) }
        ark?.let { append("ark", MystereJson.encodeToString(it)) }
        messageReference?.let { append("message_reference", MystereJson.encodeToString(it)) }
        markdown?.let { append("markdown", MystereJson.encodeToString(it)) }
        msgId?.let { append("msg_id", it) }
        eventId?.let { append("event_id", it) }
        var size = 0
        for (item in images) {
            when {
                item.startsWith("file:///") -> {
                    val path = with(item.substring(8)) {
                        if (contains(":")) this else "/$this"
                    }
                    appendFile(
                        "file_image",
                        value = FilePath(path),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        },
                    )
                }
                item.startsWith("https://") || item.startsWith("http://") -> {
                    appendHttpResp(
                        "file_image",
                        runBlocking { DefaultHttpClient.prepareGet(item) },
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        },
                    )
                }
                item.startsWith("base64://") -> {
                    append(
                        "file_image",
                        Base64.decode(item.substring(9)),
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload-${size++}.png\"")
                            append(HttpHeaders.ContentType, "image/png")
                        },
                    )
                }
                else -> {
                    log.warn { "Unknown image type: ${item.substring(0, 20)}..." }
                }
            }
        }
    })
}
