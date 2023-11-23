package io.github.mystere.qq

object QQMessageContent {
    val atReg = "<@!?(.+)>".toRegex()
    fun atReg(userId: String): String {
        return if (userId == "all") {
            "@everyone"
        } else {
            "<@$userId>"
        }
    }

    val subChanelReg = "<#(.+)>".toRegex()
    fun subChanelReg(channelId: String): String {
        return "<#$channelId>"
    }

    val emojiReg = "<emoji:(.+)>".toRegex()
    fun emojiReg(faceId: String): String {
        return "<emoji:$faceId>"
    }
}