package io.github.mystere.onebot.v11

import io.github.mystere.onebot.IOneBotConnection

abstract class IOneBotV11Connection internal constructor(
    originConfig: IConfig
): IOneBotConnection(originConfig)
