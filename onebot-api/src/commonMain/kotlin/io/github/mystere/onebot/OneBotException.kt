package io.github.mystere.onebot

class OneBotConnectionException(
    override val message: String
): RuntimeException(message)

sealed class OneBotException(
    override val message: String,
    override val cause: Throwable? = null,
): RuntimeException(message, cause)