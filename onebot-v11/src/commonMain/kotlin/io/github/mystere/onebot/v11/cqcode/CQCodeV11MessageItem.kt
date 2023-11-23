package io.github.mystere.onebot.v11.cqcode

import io.github.mystere.serialization.cqcode.CQCodeJsonClassDiscriminator
import io.github.mystere.serialization.cqcode.ICQCodeMessageItem
import io.github.mystere.serialization.cqcode.ICQCodeMessageItemOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.reflect.KClass

/**
 * CQ 码元素
 * @see <a href="https://docs.go-cqhttp.org/cqcode/#%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B">CQ 码 / CQ Code | go-cqhttp 帮助中心</a>
 */
@Serializable
sealed class CQCodeV11MessageItem(
    @Transient
    private val _typeEnum: Type = Type.text
): ICQCodeMessageItem, ICQCodeMessageItemOperator<CQCodeV11MessageItem, CQCodeV11Message> {
    // 纯文本
    @Serializable
    data class Text(
        @SerialName("text")
        val text: String
    ): CQCodeV11MessageItem()

    // QQ 表情
    @Serializable
    data class Face(
        @SerialName("id")
        val id: Long,
    ): CQCodeV11MessageItem(Type.face)

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
    ): CQCodeV11MessageItem(CQCodeV11MessageItem.Type.image) {
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
    ): CQCodeV11MessageItem(Type.record)

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
    ): CQCodeV11MessageItem(Type.video)

    // at
    @Serializable
    data class At(
        @SerialName("qq")
        val qq: String,
    ): CQCodeV11MessageItem(Type.at)

    // 猜拳魔法表情
    @Serializable
    data object Rps: CQCodeV11MessageItem(Type.rps)

    // 掷骰子魔法表情
    @Serializable
    data object Dice: CQCodeV11MessageItem(Type.dice)

    // 窗口抖动（戳一戳）
    @Serializable
    data object Shake: CQCodeV11MessageItem(Type.shake)

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
    ): CQCodeV11MessageItem(Type.poke)

    // 匿名发消息
    @Serializable
    data class Anonymous(
        @SerialName("ignore")
        val ignore: Int? = null,
    ): CQCodeV11MessageItem(Type.anonymous)

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
    ): CQCodeV11MessageItem(Type.share)

    // 推荐
    @Serializable
    data class Contact(
        @SerialName("type")
        val type: Type,
        @SerialName("id")
        val id: String,
    ): CQCodeV11MessageItem(CQCodeV11MessageItem.Type.contact) {
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
    ): CQCodeV11MessageItem(Type.location)

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
    ): CQCodeV11MessageItem(CQCodeV11MessageItem.Type.music) {
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
    ): CQCodeV11MessageItem(Type.reply)

    // 合并转发
    @Serializable
    data class Forward(
        @SerialName("id")
        val id: String,
    ): CQCodeV11MessageItem(Type.forward)

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
        val content: CQCodeV11Message? = null,
    ): CQCodeV11MessageItem(Type.node)
    fun NodeFromId(
        id: String,
    ) = Node(
        id = id,
    )
    fun NodeFromMessage(
        userId: String,
        nickname: String,
        content: CQCodeV11Message,
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
    ): CQCodeV11MessageItem(Type.xml)

    // JSON 消息
    @Serializable
    data class Json(
        @SerialName("data")
        val data: String,
    ): CQCodeV11MessageItem(Type.json)

    // 子频道
    @Serializable
    data class SubChannel(
        @SerialName("id")
        val id: String,
    ): CQCodeV11MessageItem(Type.sub_channel)

    @Transient
    override val _type: String = _typeEnum.name
    override fun ArrayDeque<CQCodeV11MessageItem>.asMessage(): CQCodeV11Message {
        return CQCodeV11Message(this)
    }

    enum class Type(
        override val `class`: KClass<out CQCodeV11MessageItem>
    ): ICQCodeMessageItem.Type {
        text(Text::class),
        face(Face::class),
        image(Image::class),
        record(Record::class),
        video(Video::class),
        at(At::class),
        rps(Rps::class),
        dice(Dice::class),
        shake(Shake::class),
        poke(Poke::class),
        anonymous(Anonymous::class),
        share(Share::class),
        contact(Contact::class),
        location(Location::class),
        music(Music::class),
        reply(Reply::class),
        forward(Forward::class),
        node(Node::class),
        xml(Xml::class),
        json(Json::class),
        sub_channel(SubChannel::class),
    }
}
