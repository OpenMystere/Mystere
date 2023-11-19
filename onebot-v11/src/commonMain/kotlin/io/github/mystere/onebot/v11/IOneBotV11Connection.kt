package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotAction
import io.github.mystere.onebot.IOneBotConnection
import kotlinx.coroutines.channels.Channel

abstract class IOneBotV11Connection internal constructor(
    originConfig: IConfig,
    actionChannel: Channel<IOneBotAction>,
): IOneBotConnection(originConfig, actionChannel)
