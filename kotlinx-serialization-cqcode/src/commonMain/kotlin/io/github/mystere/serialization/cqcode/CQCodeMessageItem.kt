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
        @SerialName("url")
        val url: String? = null,
        @SerialName("cache")
        val cache: Int? = null,
        @SerialName("proxy")
        val proxy: Int? = null,
        @SerialName("timeout")
        val timeout: Int? = null,
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
        @SerialName("proxy")
        val proxy: Int? = null,
        @SerialName("timeout")
        val timeout: Int? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.record
    }

    // 短视频
    @Serializable
    data class Video(
        @SerialName("file")
        val file: String,
        @SerialName("url")
        val url: String? = null,
        @SerialName("cache")
        val cache: Int? = null,
        @SerialName("proxy")
        val proxy: Int? = null,
        @SerialName("timeout")
        val timeout: Int? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.video
    }

    // at
    @Serializable
    data class At(
        @SerialName("qq")
        val qq: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.at
    }

    // 猜拳魔法表情
    @Serializable
    data object Rps: CQCodeMessageItem {
        override val _type: Type = Type.rps
    }

    // 掷骰子魔法表情
    @Serializable
    data object Dice: CQCodeMessageItem {
        override val _type: Type = Type.dice
    }

    // 窗口抖动（戳一戳）
    @Serializable
    data object Shake: CQCodeMessageItem {
        override val _type: Type = Type.shake
    }

    // 戳一戳
    @Serializable
    data class Poke(
        /**
         * @see <a href="https://github.com/mamoe/mirai/blob/f5eefae7ecee84d18a66afce3f89b89fe1584b78/mirai-core/src/commonMain/kotlin/net.mamoe.mirai/message/data/HummerMessage.kt#L49">Mirai 的 PokeMessage 类</a>
         */
        @SerialName("type")
        val type: String,
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.poke
    }

    // 匿名发消息
    @Serializable
    data class Anonymous(
        @SerialName("ignore")
        val ignore: Int? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.anonymous
    }

    // 链接分享
    @Serializable
    data class Share(
        @SerialName("url")
        val url: String,
        @SerialName("title")
        val title: String,
        @SerialName("content")
        val content: String? = null,
        @SerialName("image")
        val image: String? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.share
    }

    // 推荐
    @Serializable
    data class Contact(
        @SerialName("type")
        val type: Type,
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem {
        override val _type: CQCodeMessageItem.Type = CQCodeMessageItem.Type.contact

        enum class Type {
            qq, group,
        }
    }
    // 推荐好友
    fun ContactQQ(
        id: String,
    ) = Contact(
        type = Contact.Type.qq,
        id = id,
    )
    // 推荐群
    fun ContactGroup(
        id: String,
    ) = Contact(
        type = Contact.Type.group,
        id = id,
    )

    // 位置
    @Serializable
    data class Location(
        @SerialName("lat")
        val lat: String,
        @SerialName("lon")
        val lon: String,
        @SerialName("title")
        val title: String? = null,
        @SerialName("content")
        val content: String? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.location
    }

    // 音乐分享
    @Serializable
    data class Music internal constructor(
        @SerialName("type")
        val type: Type,
        @SerialName("id")
        val id: String? = null,
        @SerialName("url")
        val url: String? = null,
        @SerialName("audio")
        val audio: String? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("content")
        val content: String? = null,
        @SerialName("image")
        val image: String? = null,
    ): CQCodeMessageItem {
        override val _type: CQCodeMessageItem.Type = CQCodeMessageItem.Type.music

        enum class Type {
            qq, `163`, xm, custom
        }
    }
    // 音乐分享
    fun MusicFromId(
        type: Music.Type,
        id: String,
    ) = Music(
        type = type,
        id = id,
    )
    // 音乐自定义分享
    fun MusicCustom(
        url: String,
        audio: String,
        title: String,
        content: String? = null,
        image: String? = null,
    ) = Music(
        type = Music.Type.custom,
        url = url,
        audio = url,
        title = url,
        content = url,
        image = url,
    )

    // 回复
    @Serializable
    data class Reply(
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.reply
    }

    // 合并转发
    @Serializable
    data class Forward(
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.forward
    }

    // 合并转发节点
    @Serializable
    data class Node internal constructor(
        @SerialName("id")
        val id: String? = null,
        @SerialName("user_id")
        val userId: String? = null,
        @SerialName("nickname")
        val nickname: String? = null,
        @SerialName("content")
        val content: CQCodeMessage? = null,
    ): CQCodeMessageItem {
        override val _type: Type = Type.node
    }
    fun NodeFromId(
        id: String,
    ) = Node(
        id = id,
    )
    fun NodeFromMessage(
        userId: String,
        nickname: String,
        content: CQCodeMessage,
    ) = Node(
        userId = userId,
        nickname = nickname,
        content = content,
    )

    // XML 消息
    @Serializable
    data class Xml(
        @SerialName("data")
        val data: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.xml
    }

    // JSON 消息
    @Serializable
    data class Json(
        @SerialName("data")
        val data: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.json
    }

    // 子频道
    @Serializable
    data class SubChannel(
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem {
        override val _type: Type = Type.sub_channel
    }

    @Transient
    val _type: Type

    // TODO: 这个地方非常不优雅，暂时想不到优化方案，等待大佬 pr。。。
    enum class Type(
        val decodeFromJsonElement: (json: kotlinx.serialization.json.Json, element: JsonElement) -> CQCodeMessageItem,
        val encodeToJsonElement: (json: kotlinx.serialization.json.Json, value: CQCodeMessageItem) -> JsonElement,
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
        at(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(At.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(At.serializer(), value as At)
            },
            deserialize = { decoder ->
                At.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                At.serializer().serialize(encoder, value as At)
            },
        ),
        rps(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Rps.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Rps.serializer(), value as Rps)
            },
            deserialize = { decoder ->
                Rps.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Rps.serializer().serialize(encoder, value as Rps)
            },
        ),
        dice(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Dice.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Dice.serializer(), value as Dice)
            },
            deserialize = { decoder ->
                Dice.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Dice.serializer().serialize(encoder, value as Dice)
            },
        ),
        shake(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Shake.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Shake.serializer(), value as Shake)
            },
            deserialize = { decoder ->
                Shake.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Shake.serializer().serialize(encoder, value as Shake)
            },
        ),
        poke(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Poke.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Poke.serializer(), value as Poke)
            },
            deserialize = { decoder ->
                Poke.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Poke.serializer().serialize(encoder, value as Poke)
            },
        ),
        anonymous(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Anonymous.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Anonymous.serializer(), value as Anonymous)
            },
            deserialize = { decoder ->
                Anonymous.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Anonymous.serializer().serialize(encoder, value as Anonymous)
            },
        ),
        share(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Share.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Share.serializer(), value as Share)
            },
            deserialize = { decoder ->
                Share.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Share.serializer().serialize(encoder, value as Share)
            },
        ),
        contact(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Contact.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Contact.serializer(), value as Contact)
            },
            deserialize = { decoder ->
                Contact.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Contact.serializer().serialize(encoder, value as Contact)
            },
        ),
        location(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Location.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Location.serializer(), value as Location)
            },
            deserialize = { decoder ->
                Location.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Location.serializer().serialize(encoder, value as Location)
            },
        ),
        music(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Music.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Music.serializer(), value as Music)
            },
            deserialize = { decoder ->
                Music.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Music.serializer().serialize(encoder, value as Music)
            },
        ),
        reply(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Reply.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Reply.serializer(), value as Reply)
            },
            deserialize = { decoder ->
                Reply.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Reply.serializer().serialize(encoder, value as Reply)
            },
        ),
        forward(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Forward.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Forward.serializer(), value as Forward)
            },
            deserialize = { decoder ->
                Forward.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Forward.serializer().serialize(encoder, value as Forward)
            },
        ),
        node(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Node.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Node.serializer(), value as Node)
            },
            deserialize = { decoder ->
                Node.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Node.serializer().serialize(encoder, value as Node)
            },
        ),
        xml(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Xml.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Xml.serializer(), value as Xml)
            },
            deserialize = { decoder ->
                Xml.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Xml.serializer().serialize(encoder, value as Xml)
            },
        ),
        json(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(Json.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(Json.serializer(), value as Json)
            },
            deserialize = { decoder ->
                Json.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                Json.serializer().serialize(encoder, value as Json)
            },
        ),

        sub_channel(
            decodeFromJsonElement = { json, element ->
                json.decodeFromJsonElement(SubChannel.serializer(), element)
            },
            encodeToJsonElement = { json, value ->
                json.encodeToJsonElement(SubChannel.serializer(), value as SubChannel)
            },
            deserialize = { decoder ->
                SubChannel.serializer().deserialize(decoder)
            },
            serialize = { encoder, value ->
                SubChannel.serializer().serialize(encoder, value as SubChannel)
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
