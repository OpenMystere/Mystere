package io.github.mystere.onebot

class OneBotConnectionException(
    override val message: String
): RuntimeException(message)