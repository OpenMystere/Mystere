package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import kotlinx.coroutines.channels.Channel

abstract class IOneBotV12Connection internal constructor(
    originConfig: IConfig,
    actionChannel: Channel<IOneBotAction>,
): IOneBotConnection(originConfig, actionChannel)
