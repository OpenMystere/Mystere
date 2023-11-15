package io.github.mystere.serialization.cqcode

import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

/**
 * CQ 码元素
 * @see <a href="https://docs.go-cqhttp.org/cqcode/#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B">CQ 码 / CQ Code | go-cqhttp 帮助中心</a>
 */
@Serializable
sealed interface CQCodeMessageItem {
    @Transient
    val type: Type

    // TODO: 这个地方非常不优雅，暂时想不到优化方案，等待大佬 pr。。。
    @Serializable(with = CQCodeMessageItemTypeSerializer::class)
    enum class Type(
        val decodeFromJsonElement: (json: Json, element: JsonElement) -> CQCodeMessageItem,
        val deserialize: (decoder: Decoder) -> CQCodeMessageItem,
        val serialize: (encoder: Encoder, value: CQCodeMessageItem) -> Unit,
    ) {
        Text(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(CQCodeMessageItem.Text.serializer(), element)
            },
            deserialize = { decoder ->
                CQCodeMessageItem.Text.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                CQCodeMessageItem.Text.serializer().serialize(encoder, value as CQCodeMessageItem.Text)
            },
        ),
        Face(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(CQCodeMessageItem.Text.serializer(), element)
            },
            deserialize = { decoder ->
                CQCodeMessageItem.Face.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                CQCodeMessageItem.Face.serializer().serialize(encoder, value as CQCodeMessageItem.Face)
            },
        ),
        Record(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(CQCodeMessageItem.Text.serializer(), element)
            },
            deserialize = { decoder ->
                CQCodeMessageItem.Record.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                CQCodeMessageItem.Record.serializer().serialize(encoder, value as CQCodeMessageItem.Record)
            },
        ),
        Video(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(CQCodeMessageItem.Text.serializer(), element)
            },
            deserialize = { decoder ->
                CQCodeMessageItem.Video.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                CQCodeMessageItem.Video.serializer().serialize(encoder, value as CQCodeMessageItem.Video)
            },
        ),
        ;
    }

    operator fun plus(item: CQCodeMessageItem): CQCodeMessage {
        return CQCodeMessage(ArrayDeque<CQCodeMessageItem>().also {
            it.addLast(this)
            it.addLast(item)
        })
    }
    operator fun plus(chain: CQCodeMessage): CQCodeMessage {
        return CQCodeMessage(ArrayDeque<CQCodeMessageItem>().also {
            it.addLast(this)
            it.addAll(chain)
        })
    }

    // 纯文本
    @Serializable
    data class Text(
        @SerialName("text")
        val text: String
    ): CQCodeMessageItem {
        override val type: Type = Type.Text
    }

    // QQ 表情
    @Serializable
    data class Face(
        @SerialName("id")
        val id: Long,
    ): CQCodeMessageItem {
        override val type: Type = Type.Face
    }

    // 语音
    @Serializable
    data class Record(
        @SerialName("file")
        val file: String,
        @SerialName("magic")
        val magic: Int = 0,
        @SerialName("url")
        val url: String? = null,
        @SerialName("cache")
        val cache: Int? = null,
    ): CQCodeMessageItem {
        override val type: Type = Type.Record
    }

    // 短视频
    @Serializable
    data class Video(
        @SerialName("file")
        val file: String,
    ): CQCodeMessageItem {
        override val type: Type = Type.Video
    }
}


private val types: Map<String, CQCodeMessageItem.Type> by lazy {
    with(hashMapOf<String, CQCodeMessageItem.Type>()) {
        for (value in CQCodeMessageItem.Type.entries) {
            put(value.name.lowercase(), value)
        }
        return@with this
    }
}
internal val String.asCQCodeMessageItemType: CQCodeMessageItem.Type get() {
    return with(lowercase()) {
        types[this] ?: throw SerializationException("Not a valid CQCodeMessageItem.Type: $this")
    }
}
object CQCodeMessageItemTypeSerializer: KSerializer<CQCodeMessageItem.Type> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(
        "io.github.mystere.serialization.cqcode.CQCodeMessageItem.Type",
        SerialKind.ENUM,
    )

    override fun deserialize(decoder: Decoder): CQCodeMessageItem.Type {
        return decoder.decodeString().asCQCodeMessageItemType
    }

    override fun serialize(encoder: Encoder, value: CQCodeMessageItem.Type) {
        encoder.encodeString(value.name.lowercase())
    }
}
