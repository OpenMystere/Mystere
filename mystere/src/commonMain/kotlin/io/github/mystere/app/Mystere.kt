package io.github.mystere.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import io.github.mystere.app.util.YamlGlobal
import io.github.mystere.app.util.runBlockingWithCancellation
import io.github.mystere.core.MystereScope
import io.github.mystere.core.IMystereBot
import io.github.mystere.core.MystereCore
import io.github.mystere.onebot.v11.OneBotV11Connection
import io.github.mystere.onebot.v12.OneBotV12Connection
import io.github.mystere.util.logger
import io.github.mystere.qq.QQBot
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.yamlkt.YamlMap

@OptIn(ExperimentalStdlibApi::class)
object Mystere: CliktCommand(), AutoCloseable {
    private val log by logger()

    private val configPath: String by option("-c", "--config")
        .default("./config.yaml")
        .help("Path of config file.")
        .check(
            lazyMessage = {
                "Config file $it dose not exist or not valid."
            },
            validator = {
                SystemFileSystem.metadataOrNull(Path(it))?.isRegularFile == true
            },
        )

    private val _debug: Boolean by option("-d", "--debug")
        .flag()
        .help("Enable debug mode.")

    val Config: MystereConfig by lazy {
        YamlGlobal.decodeFromString(
            MystereConfig.serializer(),
            SystemFileSystem.source(Path(configPath)).buffered().readString(),
        )
    }

    private val bots = hashMapOf<String, IMystereBot>()

    override fun run() = runBlocking {
        log.info { "Mystere v${BuildKonfig.VERSION_NAME}(${BuildKonfig.COMMIT}) starting..." }
        if (_debug || Config.debug) {
            MystereCore.forceDebug()
        }
        if (MystereCore.Debug) {
            log.debug { "Debug mode on!" }
        }
        if (Config.bots.isEmpty()) {
            log.warn { "No bot added!" }
            return@runBlocking
        }
        for (bot in Config.bots) {
            val type = bot.type.lowercase()
            val connect = bot.connect
            val connection = when (connect.version) {
                11 -> when (connect.type) {
                    "re-ws" -> YamlGlobal.decodeFromString(
                        OneBotV11Connection.ReverseWebSocket.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "ws" -> YamlGlobal.decodeFromString(
                        OneBotV11Connection.WebSocket.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "http-post" -> YamlGlobal.decodeFromString(
                        OneBotV11Connection.HttpPost.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "http" -> YamlGlobal.decodeFromString(
                        OneBotV11Connection.Http.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    else -> throw IllegalArgumentException("Unknown OneBot v11 connection type: ${connect.type}")
                }
                12 -> when (connect.type) {
                    "re-ws" -> YamlGlobal.decodeFromString(
                        OneBotV12Connection.ReverseWebSocket.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "ws" -> YamlGlobal.decodeFromString(
                        OneBotV12Connection.WebSocket.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "http-hook" -> YamlGlobal.decodeFromString(
                        OneBotV12Connection.HttpWebhook.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    "http" -> YamlGlobal.decodeFromString(
                        OneBotV12Connection.Http.serializer(),
                        YamlGlobal.encodeToString(connect.detail),
                    )
                    else -> throw IllegalArgumentException("Unknown OneBot v12 connection type: ${connect.type}")
                }
                else -> throw IllegalArgumentException("Unknown OneBot version: ${connect.version}")
            }.createConnection()
            val botInstance = when (type) {
                "qq" -> QQBot.create(
                    YamlGlobal.decodeFromString(
                        QQBot.Config.serializer(),
                        YamlGlobal.encodeToString(bot.detail),
                    ),
                    connection,
                )
                else -> throw IllegalArgumentException("Unknown bot type: $type")
            }
            bots[botInstance.botId] = botInstance
        }
        for ((_, bot) in bots) {
            bot.connect()
        }
    }

    override fun close() {
        for ((_, bot) in bots) {
            bot.disconnect()
        }
    }
}

fun main(args: Array<String>) {
    val log = KotlinLogging.logger("Mystere")
    MystereScope.runBlockingWithCancellation(
        block = {
            Mystere.main(args)
        },
        exit = { e ->
            if (e == null) {
                log.info { "Mystere exiting..." }
            }
            Mystere.close()
            if (e != null) {
                log.error(e) { "Mystere exit unexpected!" }
            }
        },
    )
}

@Serializable
data class MystereConfig(
    @SerialName("debug")
    val debug: Boolean = false,
    @SerialName("bots")
    val bots: List<BotConfig> = listOf(),
) {
    @Serializable
    data class BotConfig(
        @SerialName("type")
        val type: String,
        @SerialName("detail")
        val detail: YamlMap,
        @SerialName("connect")
        val connect: OneBotConfig
    )
    @Serializable
    data class OneBotConfig(
        @SerialName("version")
        val version: Int = 11,
        @SerialName("type")
        val type: String,
        @SerialName("detail")
        val detail: YamlMap,
    )
}
