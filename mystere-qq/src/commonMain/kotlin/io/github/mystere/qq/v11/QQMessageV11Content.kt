package io.github.mystere.qq.v11

import io.github.mystere.core.util.smartSub
import io.github.mystere.qq.QQMessageContent
import io.github.mystere.onebot.v11.cqcode.CQCodeV11
import io.github.mystere.serialization.cqcode.CQCodeV11Message

fun String.asV11MessageContent(): CQCodeV11Message {
    return CQCodeV11.decodeFromString(
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