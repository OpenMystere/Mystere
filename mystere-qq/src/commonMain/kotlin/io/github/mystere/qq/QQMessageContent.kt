package io.github.mystere.qq

object QQMessageContent {
    val atReg = "<@!?(.+)>".toRegex()
    val subChanelReg = "<#(.+)>".toRegex()
    val emojiReg = "<emoji:(.+)>".toRegex()
}