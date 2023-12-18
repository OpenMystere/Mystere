package io.github.mystere.onebot

class OneBotConnectionException(
    override val message: String
): RuntimeException(message)

sealed class OneBotException(
    override val message: String,
    override val cause: Throwable? = null,
): RuntimeException(message, cause)

class OneBotNotImplementedException(
    val action: IOneBotAction.Action,
    override val cause: Throwable? = null,
): OneBotException("Unsupported action: ${action.name}", cause)