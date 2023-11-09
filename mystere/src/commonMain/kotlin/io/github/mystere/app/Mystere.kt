package io.github.mystere.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import io.github.mystere.app.util.runBlockingWithCancellation
import io.github.mystere.core.IMystereBot
import io.github.mystere.core.MystereCore
import io.github.mystere.core.util.logger
import io.github.mystere.qq.QQBot
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.yamlkt.Yaml
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
        Yaml.decodeFromString(
            MystereConfig.serializer(),
            SystemFileSystem.source(Path(configPath)).buffered().readString(),
        )
    }

    private val bots = hashMapOf<String, IMystereBot>()

    override fun run() {
        if (_debug || Config.debug) {
            MystereCore.forceDebug()
        }
        if (MystereCore.Debug) {
            log.info { "Debug mode on!" }
        }
        if (Config.bots.isEmpty()) {
            log.warn { "No bot added!" }
            return
        }
        for (bots in Config.bots) {
            val type = bots.getStringOrNull("type")
                ?.lowercase() ?: continue
            when (type) {
                "qq" -> QQBot.create(Yaml.decodeFromString(
                    QQBot.Config.serializer(),
                    bots.toString(),
                ))
                else -> continue
            }.let {
                this.bots[it.botId] = it
            }
        }
        for (service in Config.services) {

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
    runBlockingWithCancellation(
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
    val bots: List<YamlMap> = listOf(),
    @SerialName("services")
    val services: List<OneBotConfig> = listOf(),
) {
    @Serializable
    data class OneBotConfig(
        @SerialName("version")
        val version: String = "11",
        @SerialName("connection")
        val connection: YamlMap,
    )
}
