package io.github.mystere.onebot.v12

import io.github.mystere.onebot.IOneBotConnection
import io.github.mystere.onebot.IOneBotEvent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

abstract class IOneBotV12Connection internal constructor(
    originConfig: IConfig
): IOneBotConnection(originConfig)
