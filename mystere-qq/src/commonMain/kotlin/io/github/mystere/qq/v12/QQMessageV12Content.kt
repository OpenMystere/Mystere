package io.github.mystere.qq.v12

import io.github.mystere.onebot.v12.cqcode.CQCodeV12Message
import io.github.mystere.onebot.v12.cqcode.CQCodeV12MessageItem
import io.github.mystere.qq.QQMessageContent

fun String.asV12MessageContent(): CQCodeV12Message {
    TODO()
}

fun CQCodeV12Message.asQQMessageContent(): String {
    return StringBuilder().also {
        for (item in this) {
            when (item) {
                is CQCodeV12MessageItem.Text -> it.append(it.append(item.text))
                is CQCodeV12MessageItem.Mention -> it.append(QQMessageContent.atReg(item.userId))
                is CQCodeV12MessageItem.MentionAll -> it.append(it.append(QQMessageContent.atReg("all")))
                is CQCodeV12MessageItem.SubChannel -> it.append(it.append(QQMessageContent.subChanelReg(item.channelId)))
                else -> { }
            }
        }
    }.toString()
}
