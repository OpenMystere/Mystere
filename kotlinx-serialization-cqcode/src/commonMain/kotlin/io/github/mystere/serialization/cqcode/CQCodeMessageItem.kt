package io.github.mystere.serialization.cqcode

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * CQ 码元素
 * @see <a href="https://docs.go-cqhttp.org/cqcode/#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B">CQ 码 / CQ Code | go-cqhttp 帮助中心</a>
 */
interface CQCodeMessageItem {
    @Transient
    val _type: Type

    // TODO: 这个地方非常不优雅，暂时想不到优化方案，等待大佬 pr。。。
    enum class Type(
        val decodeFromJsonElement: (json: Json, element: JsonElement) -> CQCodeMessageItem,
        val encodeToJsonElement: (json: Json, value: CQCodeMessageItem) -> JsonElement,
        val deserialize: (decoder: Decoder) -> CQCodeMessageItem,
        val serialize: (encoder: Encoder, value: CQCodeMessageItem) -> Unit,
    ) {
        text(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Text.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Text.serializer(), value as Text)
            },
            deserialize = { decoder ->
                Text.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Text.serializer().serialize(encoder, value as Text)
            },
        ),
        face(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Face.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Face.serializer(), value as Face)
            },
            deserialize = { decoder ->
                Face.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Face.serializer().serialize(encoder, value as Face)
            },
        ),
        image(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Image.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Image.serializer(), value as Image)
            },
            deserialize = { decoder ->
                Image.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Image.serializer().serialize(encoder, value as Image)
            },
        ),
        record(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Record.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Record.serializer(), value as Record)
            },
            deserialize = { decoder ->
                Record.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Record.serializer().serialize(encoder, value as Record)
            },
        ),
        video(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Video.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Video.serializer(), value as Video)
            },
            deserialize = { decoder ->
                Video.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Video.serializer().serialize(encoder, value as Video)
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
        override val _type: Type = Type.text
    }

    // QQ 表情
    @Serializable
    data class Face(
        @SerialName("id")
        val id: Long,
    ): CQCodeMessageItem {
        override val _type: Type = Type.face
    }

    // 图片
    @Serializable
    data class Image(
        @SerialName("file")
        val file: String,
        @SerialName("type")
        val type: Type? = null,
    ): CQCodeMessageItem {
        override val _type: CQCodeMessageItem.Type = CQCodeMessageItem.Type.image

        enum class Type {
            flash,
        }
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
        override val _type: Type = Type.record
    }

    // 短视频
    @Serializable
    data class Video(
        @SerialName("file")
        val file: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.video
    }
}

fun CQCodeMessageItem.asMessage(): CQCodeMessage {
    return CQCodeMessage(ArrayDeque(listOf(this)))
}

//private val types: Map<String, CQCodeMessageItem.Type> by lazy {
//    with(hashMapOf<String, CQCodeMessageItem.Type>()) {
//        for (value in CQCodeMessageItem.Type.entries) {
//            put(value.name.lowercase(), value)
//        }
//        return@with this
//    }
//}
//internal val String.asCQCodeMessageItemType: CQCodeMessageItem.Type get() {
//    return with(lowercase()) {
//        types[this] ?: throw SerializationException("Not a valid CQCodeMessageItem.Type: $this")
//    }
//}
//object CQCodeMessageItemTypeSerializer: KSerializer<CQCodeMessageItem.Type> {
//    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
//    override val descriptor: SerialDescriptor = buildSerialDescriptor(
//        "io.github.mystere.serialization.cqcode.CQCodeMessageItem.Type",
//        SerialKind.ENUM,
//    )
//
//    override fun deserialize(decoder: Decoder): CQCodeMessageItem.Type {
//        return decoder.decodeString().asCQCodeMessageItemType
//    }
//
//    override fun serialize(encoder: Encoder, value: CQCodeMessageItem.Type) {
//        encoder.encodeString(value.name.lowercase())
//    }
//}
