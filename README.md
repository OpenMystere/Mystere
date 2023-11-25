# Mystere

[![Badge](https://img.shields.io/badge/OneBot-11-black)](https://github.com/botuniverse/onebot-11)
[![Badge](https://img.shields.io/badge/OneBot-12-black)](https://github.com/botuniverse/onebot-12)


## 介绍

Mystere 是一个基于 Kotlin/Native、实现了 OneBot 协议的机器人无头客户端。

项目名称取自[《Synduality Noir》](https://synduality-noir.com/)中的 [诺瓦尔（Noir）](https://zh.moegirl.org.cn/%E8%AF%BA%E7%93%A6%E5%B0%94) 里人格 米丝缇（Mystere），希望此项目能如米丝媂一样强大。

项目支持以下目标运行（[查看详情](#跨平台兼容性)）：

+ jvm
+ linuxX64
+ macosX64
+ macosArm64


## OneBot 协议支持

|            | WebSocket Reverse | WebSocket | Http Post | Http  |
|------------|-------------------|-----------|-----------|-------|
| OneBot V11 | 🚧️               | ❌ [1]     | ❌ [1]     | ❌ [1] |

|            | WebSocket Reverse | WebSocket | Http WebHook | Http  |
|------------|-------------------|-----------|--------------|-------|
| OneBot V12 | 🚧️               | ❌ [1]     | ❌ [1]        | ❌ [1] |

1. 适配工作尚未开始。


## 机器人平台支持

### QQ

QQ 机器人支持需要您在 [QQ 开放平台](https://q.qq.com) 创建自己的机器人才能使用。

|            | WebSocket Reverse | WebSocket | Http Post | Http  |
|------------|-------------------|-----------|-----------|-------|
| OneBot V11 | 🚧️ [2]           | ❌ [1]     | ❌ [1]     | ❌ [1] |

1. 适配工作尚未开始。
2. OneBot V11 协议需扩展才能支持，OneBot 应用端可能无法直接使用现有 OneBot V11 库，扩展内容如下：
   + 所有事件新增参数：
   
     | 字段名 | 数据类型   | 说明                                        |
     |-----|--------|-------------------------------------------|
     | id  | string | 事件唯一标识符，当事件类型为 `message` 时值同 `message_id` |
   + `send_message` 动作请求新增参数：

     | 字段名          | 数据类型           | 说明                                            |
     |--------------|----------------|-----------------------------------------------|
     | origin_event | map[string]any | 可选，回复事件，当 `origin_event` 字段存在时则为被动消息，否则为主动消息。 |

     PS：此处提到的回复不同于 [消息段中定义的回复](https://12.onebot.dev/interface/message/segments/#reply)，在 QQ 开放平台中将 [消息段中定义的回复](https://12.onebot.dev/interface/message/segments/#reply) 定义为 [消息引用（message_reference）](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/send.html)
    
     其中 `origin_event` 定义如下：
    
     | 字段名         | 数据类型   | 说明                                                                                                                         |
     |-------------|--------|----------------------------------------------------------------------------------------------------------------------------|
     | id          | string | 原事件 ID，由 Mystere 分发事件时提供                                                                                                   |
     | type        | string | 原事件类型，同 [OneBot V11 事件类型](https://github.com/botuniverse/onebot-11/tree/master/event#%E5%86%85%E5%AE%B9%E5%AD%97%E6%AE%B5) |
     | detail_type | string | 原事件详细类型 [1]                                                                                                                |
     | sub_type    | string | 原事件子类型（详细类型的下一级类型） [2]                                                                                                     |
     
     1. `detail_type` 定义如下：
        + 当 `type` 为 `meta` 时，值为原事件的 `meta_event_type`；
        + 当 `type` 为 `message` 时，值为原事件的 `message_type`；
        + 当 `type` 为 `notice` 时，值为原事件的 `notice_type`；
        + 当 `type` 为 `request` 时，值为原事件的 `request_type`。
     2. `sub_type` 定义如下：
        + 当 `type` 为 `meta` 时，值为空字符串；
        + 当 `type` 为 `message` 时，值为原事件的 `sub_type`；
        + 当 `type` 为 `notice` 时，值为原事件的 `sub_type`；
        + 当 `type` 为 `meta` 时，值为空字符串；
        简而言之就是当原事件参数存在 `sub_type` 时传递原值，否则传递空字符串。

|            | WebSocket Reverse | WebSocket | Http WebHook | Http  |
|------------|-------------------|-----------|--------------|-------|
| OneBot V12 | ❌ [1]             | ❌ [1]     | ❌ [1]        | ❌ [1] |

1. 适配工作尚未开始。
2. OneBot V12 协议需扩展才能支持，OneBot 应用端可能无法直接使用现有 OneBot V12 库，扩展内容如下：
    + `send_message` 动作请求新增参数：

      | 字段名          | 数据类型           | 说明                                            |
      |--------------|----------------|-----------------------------------------------|
      | origin_event | map[string]any | 可选，回复事件，当 `origin_event` 字段存在时则为被动消息，否则为主动消息。 |

      PS：此处提到的回复不同于 [消息段中定义的回复](https://12.onebot.dev/interface/message/segments/#reply)，在 QQ 开放平台中将 [消息段中定义的回复](https://12.onebot.dev/interface/message/segments/#reply) 定义为 [消息引用（message_reference）](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/send.html)

      其中 `origin_event` 定义如下（同 [OneBot V12 事件类型](https://12.onebot.dev/connect/data-protocol/event/)）：

      | 字段名         | 数据类型   | 说明                 |
      |-------------|--------|--------------------|
      | id          | string | 原事件 ID             |
      | type        | string | 原事件类型              |
      | detail_type | string | 原事件详细类型            |
      | sub_type    | string | 原事件子类型（详细类型的下一级类型） |
    
      此扩展内容正在申请并入 OneBot V12 草案：[查看详情](https://github.com/orgs/botuniverse/discussions/249)

## 跨平台兼容性

|                | jvm<br />Java | linuxX64<br />Linux x64 | linuxArm64<br />Linux Arm64 | macosX64<br />macOS x64 | macosArm64<br />macOS Arm64 | mingwX64<br />Windows x64 | others |
|----------------|---------------|-------------------------|-----------------------------|-------------------------|-----------------------------|---------------------------|--------|
| mystere        | ✔️            | ✔️                      | ❌ [2]                       | ✔️                      | ✔️                          | ❌ [3]                     | ❌ [1]  |
| mystere-core   | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |
| mystere-sqlite | ✔️            | ✔️                      | ❌ [4]                       | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |
| mystere-qq     | ✔️            | ✔️                      | ❌ [2]                       | ✔️                      | ✔️                          | ❌ [3]                     | ❌ [1]  |
| onebot-api     | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |
| ontbot-v11     | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ❌ [5]                     | ❌ [1]  |
| onebot-v12     | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ❌ [5]                     | ❌ [1]  |
| sdk-qq         | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |

1. 暂无计划支持其他目标。
2. 等待模块 `mystere-sqlite` 对 linuxArm64 提供支持；<br/>
   等待依赖 [com.github.ajalt.clikt:clikt](https://github.com/ajalt/clikt) 对 linuxArm64 提供支持：[arm64 - Issue #404 - ajalt/clikt](https://github.com/ajalt/clikt/issues/404)。
3. 等待模块 `ontbot-v11`、`ontbot-v12` 对 mingwX64 提供支持。
4. 等待依赖 [app.cash.sqldelight:native-driver](https://github.com/cashapp/sqldelight) 对 linuxArm64 提供支持：[Add all native targets to the runtime - Issue #4255 - cashapp/sqldelight](https://github.com/cashapp/sqldelight/issues/4255)。
5. 等待依赖 [io.ktor:ktor-server-cio](https://github.com/ktorio/ktor) 对 mingwX64 提供支持：[Native server | Ktor](https://ktor.io/docs/native-server.html)。


## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

[<img src="https://raw.githubusercontent.com/JetBrains/logos/master/web/intellij-idea/intellij-idea.svg" width="200"/>](https://www.jetbrains.com/?from=mirai)
