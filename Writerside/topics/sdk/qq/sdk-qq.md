# QQ 开放平台

OpenMystere 为您提供了对接 [QQ 开放平台](https://q.qq.com) 的能力，需要您创建自己的机器人才能使用。

## 快速开始

首先创建机器人实例并开启连接：

```Kotlin
val bot = QQBot.create {
    appId = "xxxx" // 机器人 ID
    clientSecret = "xxxx" // 机器人密钥
    private = true // 是否私域机器人
}
bot.connect()
```

机器人实例类型为 ReceiveChannel，因此您可以使用 foreach 循环接收机器人事件：

```Kotlin
for (payload in bot) {
    try {
        when (payload.opCode) {
            QQBotWebsocketPayload.OpCode.Dispatch -> when (payload.type) {
                // 频道 at 消息
                "AT_MESSAGE_CREATE" -> payload.withData<OpCode0.AtMessageCreate> {
                    // 处理事件
                }
            }
            else -> { }
        }
    } catch (e: Exception) {
        println("处理失败")
        e.printStackTrace()
    }
}
```

向 QQ 开放平台创建请求可以使用机器人实例中的 `BotAPI` 属性，例如发送频道消息：

```Kotlin
bot.BotAPI.channelsMessage(
    channelId = "xxx",
    content = "测试消息",
    msgId = id,
)
```