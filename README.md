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


## 机器人平台支持

### QQ

QQ 机器人支持需要您在 [QQ 开放平台](https://q.qq.com) 创建自己的机器人才能使用。


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
   等待 [com.github.ajalt.clikt:clikt](https://github.com/ajalt/clikt) 对 linuxArm64 提供支持：[arm64 - Issue #404 - ajalt/clikt](https://github.com/ajalt/clikt/issues/404)。
3. 等待模块 `ontbot-v11`、`ontbot-v12` 对 mingwX64 提供支持。
4. 等待 [app.cash.sqldelight:native-driver](https://github.com/cashapp/sqldelight) 对 linuxArm64 提供支持：[Add all native targets to the runtime - Issue #4255 - cashapp/sqldelight](https://github.com/cashapp/sqldelight/issues/4255)。
5. 等待 [io.ktor:ktor-server-cio](https://github.com/ktorio/ktor) 对 mingwX64 提供支持：[Native server | Ktor](https://ktor.io/docs/native-server.html)。


## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

[<img src="https://raw.githubusercontent.com/JetBrains/logos/master/web/intellij-idea/intellij-idea.svg" width="200"/>](https://www.jetbrains.com/?from=mirai)
