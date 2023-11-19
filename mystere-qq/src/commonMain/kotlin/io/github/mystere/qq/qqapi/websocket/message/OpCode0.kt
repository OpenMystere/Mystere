package io.github.mystere.qq.qqapi.websocket.message

import io.github.mystere.serialization.cqcode.CQCode
import io.github.mystere.serialization.cqcode.CQCodeMessage
import io.github.mystere.serialization.cqcode.CQCodeMessageItem
import io.github.mystere.serialization.cqcode.asMessage
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

object OpCode0 {
    @Serializable
    data class AtMessageCreate(
        @SerialName("attachments")
        val attachments: List<Attachment> = emptyList(),
        @SerialName("author")
        val author: Author,
        @SerialName("channel_id")
        val channelId: String,
        @SerialName("content")
        val content: QQMessageContent,
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("id")
        val id: String,
        @SerialName("member")
        val member: Member,
        @SerialName("mentions")
        val mentions: List<Mentions>,
        @SerialName("seq")
        val seq: Int,
        @SerialName("seq_in_channel")
        val seqInChannel: String,
        @SerialName("timestamp")
        val timestamp: Timestamp,
    ): OpCodeData {
        @Serializable
        data class Attachment(
            @SerialName("id")
            val id: String,
            @SerialName("content_type")
            val contentType: String,
            @SerialName("filename")
            val filename: String,
            @SerialName("url")
            val url: String,
            @SerialName("height")
            val height: Int? = null,
            @SerialName("width")
            val width: Int? = null,
            @SerialName("size")
            val size: Long? = null,
        )
        @Serializable
        data class Author(
            @SerialName("avatar")
            val avatar: String,
            @SerialName("bot")
            val bot: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("username")
            val username: String,
        )
        @Serializable
        data class Member(
            @SerialName("joined_at")
            val joinedAt: Timestamp,
            @SerialName("nick")
            val nick: String,
            @SerialName("roles")
            val roles: List<Int>,
        )
        @Serializable
        data class Mentions(
            @SerialName("avatar")
            val avatar: String,
            @SerialName("bot")
            val bot: Boolean,
            @SerialName("id")
            val id: String,
            @SerialName("username")
            val username: String,
        )
    }
}


@Serializable(with = QQMessageContentSerializer::class)
class QQMessageContent(
    private val delegate: List<CQCodeMessageItem>,
): List<CQCodeMessageItem> by delegate {
    val asCQCodeMessage: CQCodeMessage get() {
        var message: CQCodeMessage? = null
        for (item in delegate) {
            if (message == null) {
                message = item.asMessage()
            } else {
                message += item
            }
        }
        return message!!
    }
}

object QQMessageContentSerializer: KSerializer<QQMessageContent> {
    override val descriptor: SerialDescriptor = listSerialDescriptor<CQCodeMessageItem>()

    private val at = "<@!?(.+)>".toRegex()
    private val subChanel = "<#(.+)>".toRegex()
    private val emoji = "<emoji:(.+)>".toRegex()
    override fun deserialize(decoder: Decoder): QQMessageContent {
        return QQMessageContent(ArrayDeque(
            CQCode.decodeFromString(
                decoder.decodeString()
                    .replace("\\u003c", "<")
                    .replace("\\u003e", ">")
                    .replace("@everyone", "[CQ:at,qq=all]")
                    .replace(at) { matchResult ->
                        val userId = matchResult.groupValues[1]
                        return@replace "[CQ:at,qq=$userId]"
                    }
                    .replace(subChanel) { matchResult ->
                        val id = matchResult.groupValues[1]
                        return@replace "[CQ:sub_channel,id=$id]"
                    }
                    .replace(emoji) { matchResult ->
                        val id = matchResult.groupValues[1]
                        return@replace "[CQ:face,id=$id]"
                    }
            )
        ))
    }

    override fun serialize(encoder: Encoder, value: QQMessageContent) {
        encoder.encodeString(StringBuilder().also {
            for (item in value) {
                when (item) {
                    is CQCodeMessageItem.At -> if (item.qq == "aa") {
                        it.append("@everyone")
                    } else {
                        it.append("\\u003c@${item.qq}\\u003e")
                    }
                    is CQCodeMessageItem.SubChannel -> it.append("\\u003c#${item.id}\\u003e")
                    is CQCodeMessageItem.Face -> it.append("\\u003cemoji:${item.id}\\u003e")
                    is CQCodeMessageItem.Text -> it.append(item.text)
                }
            }
        }.toString())
    }
}

typealias Timestamp = @Serializable(with = TimestampSerializer::class) Long
object TimestampSerializer: KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): Timestamp {
        return Instant.parse(decoder.decodeString()).toEpochMilliseconds()
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(Instant.fromEpochMilliseconds(value)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString())
    }
}
