package io.github.mystere.qq.v11

import io.github.mystere.core.util.smartSub
import io.github.mystere.qq.QQMessageContent
import io.github.mystere.onebot.v11.cqcode.CQCodeV11Message
import io.github.mystere.onebot.v11.cqcode.CQCodeV11MessageItem
import io.github.mystere.onebot.v11.cqcode.decodeV11FromString
import io.github.mystere.qqsdk.qqapi.dto.MessageReference
import io.github.mystere.serialization.cqcode.CQCode

fun String.asV11MessageContent(): CQCodeV11Message {
    return CQCode.decodeV11FromString(
        this.replace(QQMessageContent.subChanelReg, "")
            .replace("@everyone", "[CQ:at,qq=all]")
            .replace(QQMessageContent.atReg) {
                "[CQ:at,qq=${it.groupValues[0]
                    .replace("!", "")
                    .smartSub(2, -1)}]"
            }.replace(QQMessageContent.emojiReg) {
                "[CQ:face,id=${it.groupValues[0]
                    .smartSub(8, -1)}]"
            }
    )
}

fun CQCodeV11Message.asQQMessageContent(): String? {
    return StringBuilder().also {
        for (item in this) {
            when (item) {
                is CQCodeV11MessageItem.Text -> it.append(it.append(item.text))
                is CQCodeV11MessageItem.At -> it.append(QQMessageContent.atReg(item.qq))
                is CQCodeV11MessageItem.SubChannel -> it.append(it.append(QQMessageContent.subChanelReg(item.channelId)))
                else -> { }
            }
        }
    }.toString()
}

fun CQCodeV11Message.asQQImageList(): List<String> {
    return ArrayDeque<String>().also {
        for (item in this) {
            if (item !is CQCodeV11MessageItem.Image) {
                continue
            }
            it.addLast(item.file)
        }
    }
}

fun CQCodeV11Message.asQQMessageReference(): MessageReference? {
    var reference: MessageReference? = null
    for (item in this) {
        if (item is CQCodeV11MessageItem.Reply) {
            reference = MessageReference(
                messageId = item.id,
                ignoreGetMessageError = false,
            )
            break
        }
    }
    return reference
}
