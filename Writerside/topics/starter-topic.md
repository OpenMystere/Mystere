# 介绍

<!--Writerside adds this topic when you create a new documentation project.
You can use it as a sandbox to play with Writerside features, and remove it from the TOC when you don't need it anymore.-->

[![Badge](https://img.shields.io/badge/OneBot-11-black)](https://github.com/botuniverse/onebot-11)
[![Badge](https://img.shields.io/badge/OneBot-12-black)](https://github.com/botuniverse/onebot-12)

Mystere 是一个基于 Kotlin/Native、实现了 OneBot 协议的机器人无头客户端。

项目名称取自[《Synduality Noir》](https://synduality-noir.com/)中的 [诺瓦尔（Noir）](https://zh.moegirl.org.cn/诺瓦尔) 里人格 米丝缇（Mystere），希望此项目能如米丝媂一样强大。

项目支持以下目标运行（[查看详情](#跨平台兼容性)）：

+ jvm
+ linuxX64
+ macosX64
+ macosArm64


## 跨平台兼容性 {id="跨平台兼容性"}

|              | jvm<br />Java | linuxX64<br />Linux x64 | linuxArm64<br />Linux Arm64 | macosX64<br />macOS x64 | macosArm64<br />macOS Arm64 | mingwX64<br />Windows x64 | others |
|--------------|---------------|-------------------------|-----------------------------|-------------------------|-----------------------------|---------------------------|--------|
| mystere      | ✔️            | ✔️                      | ❌ [2]                       | ✔️                      | ✔️                          | ❌ [3]                     | ❌ [1]  |
| mystere-core | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |
| mystere-qq   | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ❌ [3]                     | ❌ [1]  |
| onebot-api   | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |
| ontbot-v11   | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ❌ [4]                     | ❌ [1]  |
| onebot-v12   | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ❌ [4]                     | ❌ [1]  |
| sdk-qq       | ✔️            | ✔️                      | ✔️                          | ✔️                      | ✔️                          | ✔️                        | ❌ [1]  |

1. 暂无计划支持其他目标。
2. 等待依赖 [com.github.ajalt.clikt:clikt](https://github.com/ajalt/clikt) 对 linuxArm64 提供支持：[arm64 - Issue #404 - ajalt/clikt](https://github.com/ajalt/clikt/issues/404)。
3. 等待模块 `ontbot-v11`、`ontbot-v12` 对 mingwX64 提供支持。
4. 等待依赖 [io.ktor:ktor-server-cio](https://github.com/ktorio/ktor) 对 mingwX64 提供支持：[Native server | Ktor](https://ktor.io/docs/native-server.html)。
