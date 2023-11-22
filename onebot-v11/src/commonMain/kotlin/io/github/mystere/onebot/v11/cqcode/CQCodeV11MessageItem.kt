package io.github.mystere.onebot.v11.cqcode

import io.github.mystere.serialization.cqcode.CQCodeMessage
import io.github.mystere.serialization.cqcode.CQCodeMessageItem
import kotlinx.serialization.*

/**
 * CQ 码元素
 * @see <a href="https://docs.go-cqhttp.org/cqcode/#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B">CQ 码 / CQ Code | go-cqhttp 帮助中心</a>
 */
object CQCodeV11MessageItem {
    // QQ 表情
    @Serializable
    data class Face(
        @SerialName("id")
        val id: Long,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Face)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Image) {
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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Record)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Video)

    // at
    @Serializable
    data class At(
        @SerialName("qq")
        val qq: String,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.At)

    // 猜拳魔法表情
    @Serializable
    data object Rps: CQCodeMessageItem(CQCodeV11MessageItem.Type.Rps)

    // 掷骰子魔法表情
    @Serializable
    data object Dice: CQCodeMessageItem(CQCodeV11MessageItem.Type.Dice)

    // 窗口抖动（戳一戳）
    @Serializable
    data object Shake: CQCodeMessageItem(CQCodeV11MessageItem.Type.Shake)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Poke)

    // 匿名发消息
    @Serializable
    data class Anonymous(
        @SerialName("ignore")
        val ignore: Int? = null,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Anonymous)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Share)

    // 推荐
    @Serializable
    data class Contact(
        @SerialName("type")
        val type: Type,
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Contact) {
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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Location)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Music) {
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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Reply)

    // 合并转发
    @Serializable
    data class Forward(
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Forward)

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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Node)
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
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Xml)

    // JSON 消息
    @Serializable
    data class Json(
        @SerialName("data")
        val data: String,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.Json)

    // 子频道
    @Serializable
    data class SubChannel(
        @SerialName("id")
        val id: String,
    ): CQCodeMessageItem(CQCodeV11MessageItem.Type.SubChannel)

    object Type {
        object Face: CQCodeMessageItem.Type<CQCodeV11MessageItem.Face>(CQCodeV11MessageItem.Face.serializer())
        object Image: CQCodeMessageItem.Type<CQCodeV11MessageItem.Image>(CQCodeV11MessageItem.Image.serializer())
        object Record: CQCodeMessageItem.Type<CQCodeV11MessageItem.Record>(CQCodeV11MessageItem.Record.serializer())
        object Video: CQCodeMessageItem.Type<CQCodeV11MessageItem.Video>(CQCodeV11MessageItem.Video.serializer())
        object At: CQCodeMessageItem.Type<CQCodeV11MessageItem.At>(CQCodeV11MessageItem.At.serializer())
        object Rps: CQCodeMessageItem.Type<CQCodeV11MessageItem.Rps>(CQCodeV11MessageItem.Rps.serializer())
        object Dice: CQCodeMessageItem.Type<CQCodeV11MessageItem.Dice>(CQCodeV11MessageItem.Dice.serializer())
        object Shake: CQCodeMessageItem.Type<CQCodeV11MessageItem.Shake>(CQCodeV11MessageItem.Shake.serializer())
        object Poke: CQCodeMessageItem.Type<CQCodeV11MessageItem.Poke>(CQCodeV11MessageItem.Poke.serializer())
        object Anonymous: CQCodeMessageItem.Type<CQCodeV11MessageItem.Anonymous>(CQCodeV11MessageItem.Anonymous.serializer())
        object Share: CQCodeMessageItem.Type<CQCodeV11MessageItem.Share>(CQCodeV11MessageItem.Share.serializer())
        object Contact: CQCodeMessageItem.Type<CQCodeV11MessageItem.Contact>(CQCodeV11MessageItem.Contact.serializer())
        object Location: CQCodeMessageItem.Type<CQCodeV11MessageItem.Location>(CQCodeV11MessageItem.Location.serializer())
        object Music: CQCodeMessageItem.Type<CQCodeV11MessageItem.Music>(CQCodeV11MessageItem.Music.serializer())
        object Reply: CQCodeMessageItem.Type<CQCodeV11MessageItem.Reply>(CQCodeV11MessageItem.Reply.serializer())
        object Forward: CQCodeMessageItem.Type<CQCodeV11MessageItem.Forward>(CQCodeV11MessageItem.Forward.serializer())
        object Node: CQCodeMessageItem.Type<CQCodeV11MessageItem.Node>(CQCodeV11MessageItem.Node.serializer())
        object Xml: CQCodeMessageItem.Type<CQCodeV11MessageItem.Xml>(CQCodeV11MessageItem.Xml.serializer())
        object Json: CQCodeMessageItem.Type<CQCodeV11MessageItem.Json>(CQCodeV11MessageItem.Json.serializer())
        object SubChannel: CQCodeMessageItem.Type<CQCodeV11MessageItem.SubChannel>(CQCodeV11MessageItem.SubChannel.serializer())
    }
}
