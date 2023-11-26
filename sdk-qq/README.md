## QQ SDK

此模块提供了对接 [QQ 开放平台](https://bot.q.qq.com/wiki/develop/api-v2/) 的能力。

### 示例代码

```kotlin
import io.github.mystere.qqsdk.qqapi.http.channelsMessage
import io.github.mystere.qqsdk.qqapi.websocket.QQBotWebsocketPayload
import io.github.mystere.qqsdk.qqapi.websocket.message.OpCode0
import io.github.mystere.qqsdk.qqapi.websocket.withData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking {
    val bot = QQBot.create {
        appId = "xxxx" // 机器人 ID
        clientSecret = "xxxx" // 机器人密钥
        private = true // 是否私域机器人
    }
    bot.connect()

    bot.use {
        for (payload in bot) {
            try {
                when (payload.opCode) {
                    QQBotWebsocketPayload.OpCode.Dispatch -> when (payload.type) {
                        "AT_MESSAGE_CREATE" -> payload.withData<OpCode0.AtMessageCreate> {
                            it.BotAPI.channelsMessage(
                                channelId = "xxx",
                                content = "测试消息",
                                msgId = id,
                            )
                        }
                    }
                    else -> { }
                }
            } catch (e: Exception) {
                println("处理失败")
                e.printStackTrace()
            }
        }
    }
}
```