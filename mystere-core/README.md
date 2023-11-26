## mystere-core

此模块为 Mystere 核心模块。

### 示例代码

开发者可利用此模块创建自己的协议处理逻辑~~（这好像是徒手造了个简易版 Netty？）~~，示例代码：

```kotlin
import io.github.mystere.core.util.logger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
class MyConnection(
    ownBotId: String
): IMystereBotConnection<JsonObject, JsonObject>(ownBotId) {
    private val coroutineScope by lazyMystereScope()
    private val log by logger()

    override suspend fun connect(httpClient: HttpClientConfig<*>.() -> Unit) {
        coroutineScope.launch {
            while (true) {
                try {
                    // 对接其他平台接收事件
                    eventChannel.send(buildJsonObject {
                        put("event", JsonPrimitive("message_event"))
                    })
                    delay(10_000)
                } catch (e: Exception) {
                    log.warn(e) { "事件分发失败" }
                }
            }
        }
        coroutineScope.launch {
            for (action in actionChannel) {
                try {
                    // 提交动作，例如对接 QQ 开放平台发送消息
                } catch (e: Exception) {
                    log.warn(e) { "动作执行失败" }
                }
            }
        }
    }

    override suspend fun disconnect() {
        coroutineScope.cancel()
    }
}

fun main() = runBlocking {
    val log = KotlinLogging.logger("GlobalLogger")
    val connection = TestConnection("my-bot")
    for (event in connection) {
        try {
            // 处理事件，并向连接器提交动作执行申请
            // ...
            connection.send(buildJsonObject {
                put("action", JsonPrimitive("message_action"))
            })
        } catch (e: Exception) {
            log.warn(e) { "动作提交失败" }
        }
    }
}

```